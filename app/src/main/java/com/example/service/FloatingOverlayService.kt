package com.example.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.data.dictionary.LocalDictionary
import com.example.data.local.AppDatabase
import com.example.data.remote.GeminiTranslatorClient
import com.example.repository.TranslatorRepository
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import java.util.Locale
import kotlin.math.abs

class FloatingOverlayService : Service() {

  companion object {
    private const val TAG = "FloatingOverlayService"
    const val ACTION_START = "com.example.service.START"
    const val ACTION_STOP = "com.example.service.STOP"
    const val ACTION_UPDATE_PROJECTION = "com.example.service.UPDATE_PROJECTION"
    const val CHANNEL_ID = "layar_terjemah_overlay_channel"
    const val NOTIFICATION_ID = 1001

    @Volatile
    var isRunning = false
      private set

    var projectionResultCode: Int = 0
      private set
    var projectionResultData: Intent? = null
      private set

    fun setProjectionData(resultCode: Int, data: Intent?) {
      projectionResultCode = resultCode
      projectionResultData = data
    }

    val isScreenCaptureReady: Boolean
      get() = projectionResultCode != 0 && projectionResultData != null

    fun startService(context: Context) {
      if (!Settings.canDrawOverlays(context)) {
        Toast.makeText(context, "Izin Tampilkan di Atas Aplikasi Lain dibutuhkan", Toast.LENGTH_SHORT).show()
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
          addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
        return
      }

      val intent = Intent(context, FloatingOverlayService::class.java).apply {
        action = ACTION_START
      }
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        context.startForegroundService(intent)
      } else {
        context.startService(intent)
      }
    }

    fun stopService(context: Context) {
      val intent = Intent(context, FloatingOverlayService::class.java).apply {
        action = ACTION_STOP
      }
      context.startService(intent)
    }
  }

  private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
  private val mainHandler = Handler(Looper.getMainLooper())

  private var windowManager: WindowManager? = null
  private var floatingBubbleView: View? = null
  private var translationCardOverlayView: View? = null
  private var inPlaceBadgesOverlayView: View? = null

  private var bubbleParams: WindowManager.LayoutParams? = null
  private var cardParams: WindowManager.LayoutParams? = null

  private var mediaProjection: MediaProjection? = null
  private var virtualDisplay: VirtualDisplay? = null
  private var imageReader: ImageReader? = null
  @Volatile
  private var latestScreenBitmap: Bitmap? = null
  private var captureThread: HandlerThread? = null
  private var captureHandler: Handler? = null
  @Volatile
  private var isCaptureRequested = false
  @Volatile
  private var captureCallback: ((Bitmap?) -> Unit)? = null

  private var tts: TextToSpeech? = null
  private var isOverlayCardVisible = false
  private var isCapturing = false
  private var repository: TranslatorRepository? = null

  override fun onBind(intent: Intent?): IBinder? = null

  override fun onCreate() {
    super.onCreate()
    GeminiTranslatorClient.init(this)
    createNotificationChannel()
    val db = AppDatabase.getDatabase(this)
    repository = TranslatorRepository(db.translationDao())

    val thread = HandlerThread("OverlayCaptureThread")
    thread.start()
    captureThread = thread
    captureHandler = Handler(thread.looper)

    tts = TextToSpeech(this) { status ->
      if (status == TextToSpeech.SUCCESS) {
        tts?.language = Locale.forLanguageTag("id-ID")
      }
    }
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    when (intent?.action) {
      ACTION_STOP -> {
        stopSelf()
        return START_NOT_STICKY
      }
      ACTION_UPDATE_PROJECTION -> {
        startServiceForeground()
        setupMediaProjectionCapture()
      }
      ACTION_START, null -> {
        startServiceForeground()
        if (!isRunning) {
          setupFloatingBubble()
          isRunning = true
        }
        setupMediaProjectionCapture()
      }
    }
    return START_STICKY
  }

  private fun startServiceForeground() {
    val notification = createNotification()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
      val type = if (isScreenCaptureReady) {
        ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
      } else {
        ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
      }
      try {
        startForeground(NOTIFICATION_ID, notification, type)
      } catch (e: Exception) {
        startForeground(NOTIFICATION_ID, notification)
      }
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      if (isScreenCaptureReady) {
        try {
          startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION)
        } catch (e: Exception) {
          startForeground(NOTIFICATION_ID, notification)
        }
      } else {
        startForeground(NOTIFICATION_ID, notification)
      }
    } else {
      startForeground(NOTIFICATION_ID, notification)
    }
  }

  /**
   * Sets up real MediaProjection and VirtualDisplay with background ImageReader listener.
   * Frames are dropped instantly with zero allocations unless isCaptureRequested is true.
   */
  private fun setupMediaProjectionCapture() {
    if (!isScreenCaptureReady) return

    val code = projectionResultCode
    val data = projectionResultData ?: return

    try {
      if (mediaProjection == null) {
        val mpManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        mediaProjection = mpManager.getMediaProjection(code, data)
        mediaProjection?.registerCallback(object : MediaProjection.Callback() {
          override fun onStop() {
            super.onStop()
            Log.d(TAG, "MediaProjection stopped")
            cleanUpCaptureResources()
            setProjectionData(0, null)
          }
        }, mainHandler)
      }

      val wm = getSystemService(WINDOW_SERVICE) as WindowManager
      val (width, height, densityDpi) = getRealScreenDimensions(wm)

      if (imageReader == null || virtualDisplay == null) {
        val handler = captureHandler ?: mainHandler
        imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2)
        imageReader?.setOnImageAvailableListener({ reader ->
          val image = try {
            reader.acquireLatestImage()
          } catch (t: Throwable) {
            null
          } ?: return@setOnImageAvailableListener

          try {
            if (isCaptureRequested) {
              isCaptureRequested = false
              val planes = image.planes
              val buffer = planes[0].buffer
              val pixelStride = planes[0].pixelStride
              val rowStride = planes[0].rowStride
              val rowPadding = rowStride - pixelStride * width

              val bmp = Bitmap.createBitmap(
                width + rowPadding / pixelStride,
                height,
                Bitmap.Config.ARGB_8888
              )
              bmp.copyPixelsFromBuffer(buffer)

              val cropped = if (rowPadding == 0) bmp else Bitmap.createBitmap(bmp, 0, 0, width, height)
              latestScreenBitmap = cropped

              val cb = captureCallback
              captureCallback = null
              cb?.invoke(cropped)
            }
          } catch (t: Throwable) {
            t.printStackTrace()
            val cb = captureCallback
            captureCallback = null
            cb?.invoke(null)
          } finally {
            image.close()
          }
        }, handler)

        virtualDisplay = mediaProjection?.createVirtualDisplay(
          "LayarTerjemahStream",
          width, height, densityDpi,
          DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
          imageReader?.surface, null, handler
        )
      }
    } catch (e: Exception) {
      Log.e(TAG, "Error setting up MediaProjection: ${e.message}", e)
    }
  }

  private fun getRealScreenDimensions(wm: WindowManager): Triple<Int, Int, Int> {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      val bounds = wm.currentWindowMetrics.bounds
      val dpi = resources.configuration.densityDpi
      Triple(bounds.width(), bounds.height(), dpi)
    } else {
      val dm = DisplayMetrics()
      @Suppress("DEPRECATION")
      wm.defaultDisplay.getRealMetrics(dm)
      Triple(dm.widthPixels, dm.heightPixels, dm.densityDpi)
    }
  }

  private fun cleanUpCaptureResources() {
    virtualDisplay?.release()
    virtualDisplay = null
    imageReader?.close()
    imageReader = null
    mediaProjection = null
  }

  @SuppressLint("ClickableViewAccessibility")
  private fun setupFloatingBubble() {
    if (!Settings.canDrawOverlays(this)) {
      stopSelf()
      return
    }

    windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

    val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
    } else {
      @Suppress("DEPRECATION")
      WindowManager.LayoutParams.TYPE_PHONE
    }

    bubbleParams = WindowManager.LayoutParams(
      WindowManager.LayoutParams.WRAP_CONTENT,
      WindowManager.LayoutParams.WRAP_CONTENT,
      layoutFlag,
      WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
          WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
      PixelFormat.TRANSLUCENT
    ).apply {
      gravity = Gravity.TOP or Gravity.START
      x = 50
      y = 350
    }

    val bubbleSize = (60 * resources.displayMetrics.density).toInt()

    val container = FrameLayout(this).apply {
      layoutParams = FrameLayout.LayoutParams(bubbleSize, bubbleSize)
      val bg = GradientDrawable().apply {
        shape = GradientDrawable.OVAL
        setColor(0xFF0D9488.toInt())
        setStroke((2 * resources.displayMetrics.density).toInt(), 0xFF5EEAD4.toInt())
      }
      background = bg
      elevation = 16f
    }

    val iconText = TextView(this).apply {
      text = "EN→ID"
      setTextColor(Color.WHITE)
      textSize = 12f
      typeface = android.graphics.Typeface.DEFAULT_BOLD
      gravity = Gravity.CENTER
      layoutParams = FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.MATCH_PARENT,
        FrameLayout.LayoutParams.MATCH_PARENT
      )
    }
    container.addView(iconText)

    var initialX = 0
    var initialY = 0
    var initialTouchX = 0f
    var initialTouchY = 0f
    var isClick = true

    container.setOnTouchListener { _, event ->
      when (event.action) {
        MotionEvent.ACTION_DOWN -> {
          initialX = bubbleParams?.x ?: 0
          initialY = bubbleParams?.y ?: 0
          initialTouchX = event.rawX
          initialTouchY = event.rawY
          isClick = true
          true
        }
        MotionEvent.ACTION_MOVE -> {
          val dx = (event.rawX - initialTouchX).toInt()
          val dy = (event.rawY - initialTouchY).toInt()
          if (abs(dx) > 10 || abs(dy) > 10) {
            isClick = false
          }
          bubbleParams?.x = initialX + dx
          bubbleParams?.y = initialY + dy
          windowManager?.updateViewLayout(container, bubbleParams)
          true
        }
        MotionEvent.ACTION_UP -> {
          if (isClick) {
            handleBubbleClick()
          }
          true
        }
        else -> false
      }
    }

    floatingBubbleView = container
    try {
      windowManager?.addView(floatingBubbleView, bubbleParams)
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  private fun handleBubbleClick() {
    if (isOverlayCardVisible) {
      hideTranslationCardOverlay()
      return
    }

    if (!isScreenCaptureReady) {
      showPermissionRequiredCard()
      return
    }

    captureRealScreenAndTranslate()
  }

  /**
   * Captures the real screen frame and runs Google ML Kit Text Recognition on it.
   */
  private fun captureRealScreenAndTranslate() {
    if (isCapturing) return
    isCapturing = true

    setupMediaProjectionCapture()

    // Hide bubble momentarily so it is not in the screenshot
    floatingBubbleView?.visibility = View.INVISIBLE

    serviceScope.launch {
      // Allow window manager to process bubble invisibility before taking snapshot
      delay(120)

      var bitmapToProcess: Bitmap? = null

      withContext(Dispatchers.IO) {
        kotlinx.coroutines.suspendCancellableCoroutine<Bitmap?> { cont ->
          captureCallback = { bmp ->
            if (cont.isActive) cont.resume(bmp)
          }
          isCaptureRequested = true

          // Fallback timeout after 1.2s if no new frame emitted
          serviceScope.launch {
            delay(1200)
            if (cont.isActive) {
              captureCallback = null
              cont.resume(latestScreenBitmap)
            }
          }
        }
      }.also { bitmapToProcess = it }

      // Restore bubble
      floatingBubbleView?.visibility = View.VISIBLE
      isCapturing = false

      if (bitmapToProcess == null) {
        showTranslationResultCard(
          original = "Izin MediaProjection Belum Aktif Sepenuhnya",
          translated = "Ketuk 'Izinkan Sekarang' untuk memperbarui izin tangkapan layar sistem.",
          isLoading = false,
          isPermissionPrompt = true
        )
        return@launch
      }

      // Show scan indicator card now that the clean frame has been captured
      showTranslationResultCard(
        original = "Memindai teks pada layar...",
        translated = "Sedang membaca teks dengan Google ML Kit OCR...",
        isLoading = true
      )

      // Run Google ML Kit Text Recognition on the REAL captured screen bitmap
      try {
        val inputImage = InputImage.fromBitmap(bitmapToProcess, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(inputImage)
          .addOnSuccessListener { visionText ->
            val detectedText = visionText.text.trim()
            if (detectedText.isEmpty()) {
              showTranslationResultCard(
                original = "Tidak ada teks bahasa Inggris terdeteksi",
                translated = "Layar saat ini tidak memiliki teks yang terbaca. Coba buka teks artikel, chat, atau game.",
                isLoading = false
              )
            } else {
              serviceScope.launch {
                val translated = withContext(Dispatchers.IO) {
                  GeminiTranslatorClient.translateText(detectedText)
                }

                showTranslationResultCard(
                  original = detectedText,
                  translated = translated,
                  isLoading = false
                )

                // Render in-place overlay badges on screen
                val wm = getSystemService(WINDOW_SERVICE) as WindowManager
                val (screenWidth, screenHeight, _) = getRealScreenDimensions(wm)
                renderInPlaceBadgesOnScreen(visionText.textBlocks, screenWidth, screenHeight)

                // Save to Room DB
                repository?.saveToHistory(
                  original = detectedText,
                  translated = translated,
                  sourceContext = "Layar Nyata (MediaProjection)",
                  engine = "Gemini 3.5 Live Translate",
                  count = visionText.textBlocks.size
                )
              }
            }
          }
          .addOnFailureListener { e ->
            showTranslationResultCard(
              original = "Gagal memproses OCR",
              translated = "Error: ${e.message}",
              isLoading = false
            )
          }
          .addOnCompleteListener {
            recognizer.close()
          }
      } catch (e: Exception) {
        showTranslationResultCard(
          original = "Kesalahan Sistem OCR",
          translated = "Error: ${e.message}",
          isLoading = false
        )
      }
    }
  }

  private fun renderInPlaceBadgesOnScreen(
    blocks: List<com.google.mlkit.vision.text.Text.TextBlock>,
    screenWidth: Int,
    screenHeight: Int
  ) {
    removeInPlaceBadges()

    val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
    } else {
      @Suppress("DEPRECATION")
      WindowManager.LayoutParams.TYPE_PHONE
    }

    val overlayContainer = FrameLayout(this).apply {
      layoutParams = FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.MATCH_PARENT,
        FrameLayout.LayoutParams.MATCH_PARENT
      )
    }

    val density = resources.displayMetrics.density

    // Create readable Indonesian badges for top detected text blocks
    blocks.take(8).forEach { block ->
      val rect = block.boundingBox ?: return@forEach
      val original = block.text.trim()
      if (original.isEmpty()) return@forEach

      val translated = LocalDictionary.translateOffline(original)

      val badgeView = TextView(this).apply {
        text = translated
        setTextColor(Color.WHITE)
        textSize = 11f
        typeface = android.graphics.Typeface.DEFAULT_BOLD
        val pad = (4 * density).toInt()
        setPadding(pad * 2, pad, pad * 2, pad)
        val bg = GradientDrawable().apply {
          cornerRadius = 6 * density
          setColor(0xEE0F172A.toInt())
          setStroke((1 * density).toInt(), 0xFF2DD4BF.toInt())
        }
        background = bg
        maxWidth = (screenWidth - 48).coerceAtLeast(120)

        val safeLeft = rect.left.coerceIn(8, (screenWidth - 100).coerceAtLeast(8))
        val safeTop = rect.top.coerceIn(16, (screenHeight - 60).coerceAtLeast(16))

        val lp = FrameLayout.LayoutParams(
          FrameLayout.LayoutParams.WRAP_CONTENT,
          FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply {
          leftMargin = safeLeft
          topMargin = safeTop
        }
        layoutParams = lp
      }

      overlayContainer.addView(badgeView)
    }

    val overlayParams = WindowManager.LayoutParams(
      WindowManager.LayoutParams.MATCH_PARENT,
      WindowManager.LayoutParams.MATCH_PARENT,
      layoutFlag,
      WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
          WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
      PixelFormat.TRANSLUCENT
    )

    inPlaceBadgesOverlayView = overlayContainer
    try {
      windowManager?.addView(inPlaceBadgesOverlayView, overlayParams)
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  private fun removeInPlaceBadges() {
    if (inPlaceBadgesOverlayView != null) {
      try {
        windowManager?.removeView(inPlaceBadgesOverlayView)
      } catch (e: Exception) {
        e.printStackTrace()
      }
      inPlaceBadgesOverlayView = null
    }
  }

  private fun showPermissionRequiredCard() {
    showTranslationResultCard(
      original = "Izin Rekam Layar (MediaProjection) Belum Diberikan",
      translated = "Ketuk 'Izinkan Sekarang' untuk memberikan izin sistem Android.",
      isLoading = false,
      isPermissionPrompt = true
    )
  }

  private fun showTranslationResultCard(
    original: String,
    translated: String,
    isLoading: Boolean,
    isPermissionPrompt: Boolean = false
  ) {
    if (translationCardOverlayView != null) {
      hideTranslationCardOverlay()
    }

    val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
    } else {
      @Suppress("DEPRECATION")
      WindowManager.LayoutParams.TYPE_PHONE
    }

    cardParams = WindowManager.LayoutParams(
      WindowManager.LayoutParams.MATCH_PARENT,
      WindowManager.LayoutParams.WRAP_CONTENT,
      layoutFlag,
      WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
      PixelFormat.TRANSLUCENT
    ).apply {
      gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
      x = 0
      y = (16 * resources.displayMetrics.density).toInt()
    }

    val density = resources.displayMetrics.density

    val cardContainer = LinearLayout(this).apply {
      orientation = LinearLayout.VERTICAL
      val pad = (14 * density).toInt()
      setPadding(pad, pad, pad, pad)
      val bg = GradientDrawable().apply {
        cornerRadius = 20 * density
        setColor(0xF00F172A.toInt())
        setStroke((1.5f * density).toInt(), 0xFF14B8A6.toInt())
      }
      background = bg
      elevation = 20f

      val marginParams = FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.MATCH_PARENT,
        FrameLayout.LayoutParams.WRAP_CONTENT
      ).apply {
        val marginSide = (12 * density).toInt()
        setMargins(marginSide, 0, marginSide, (20 * density).toInt())
      }
      layoutParams = marginParams
    }

    // Header
    val headerRow = LinearLayout(this).apply {
      orientation = LinearLayout.HORIZONTAL
      gravity = Gravity.CENTER_VERTICAL
      layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
      )
    }

    val titleText = TextView(this).apply {
      text = if (isLoading) "⚡ Memindai Layar..." else "⚡ Hasil Terjemahan Layar (EN→ID)"
      setTextColor(0xFF14B8A6.toInt())
      textSize = 13f
      typeface = android.graphics.Typeface.DEFAULT_BOLD
      layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
    }
    headerRow.addView(titleText)

    val closeBtn = TextView(this).apply {
      text = "✕ Tutup"
      setTextColor(0xFF94A3B8.toInt())
      textSize = 12f
      typeface = android.graphics.Typeface.DEFAULT_BOLD
      setPadding((8 * density).toInt(), (4 * density).toInt(), (8 * density).toInt(), (4 * density).toInt())
      val btnBg = GradientDrawable().apply {
        cornerRadius = 8 * density
        setColor(0xFF1E293B.toInt())
      }
      background = btnBg
      setOnClickListener { hideTranslationCardOverlay() }
    }
    headerRow.addView(closeBtn)
    cardContainer.addView(headerRow)

    // Body Box
    val contentBox = LinearLayout(this).apply {
      orientation = LinearLayout.VERTICAL
      val padBox = (10 * density).toInt()
      setPadding(padBox, padBox, padBox, padBox)
      val boxBg = GradientDrawable().apply {
        cornerRadius = 12 * density
        setColor(0x40134E4A.toInt())
        setStroke((1 * density).toInt(), 0xFF2DD4BF.toInt())
      }
      background = boxBg
      layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
      ).apply {
        topMargin = (8 * density).toInt()
      }
    }

    val translatedLabel = TextView(this).apply {
      text = "BAHASA INDONESIA:"
      setTextColor(0xFF5EEAD4.toInt())
      textSize = 10f
      typeface = android.graphics.Typeface.DEFAULT_BOLD
    }
    contentBox.addView(translatedLabel)

    val translatedTextView = TextView(this).apply {
      text = translated
      setTextColor(Color.WHITE)
      textSize = 14f
      typeface = android.graphics.Typeface.DEFAULT_BOLD
      setPadding(0, (2 * density).toInt(), 0, (4 * density).toInt())
    }
    contentBox.addView(translatedTextView)

    val originalTextView = TextView(this).apply {
      text = original
      setTextColor(0xFF94A3B8.toInt())
      textSize = 11f
      maxLines = 4
    }
    contentBox.addView(originalTextView)
    cardContainer.addView(contentBox)

    // Action buttons
    val actionsRow = LinearLayout(this).apply {
      orientation = LinearLayout.HORIZONTAL
      gravity = Gravity.CENTER_VERTICAL
      layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
      ).apply {
        topMargin = (8 * density).toInt()
      }
    }

    if (isPermissionPrompt) {
      val grantBtn = TextView(this).apply {
        text = "🛡️ Izinkan Sekarang"
        setTextColor(Color.WHITE)
        textSize = 12f
        typeface = android.graphics.Typeface.DEFAULT_BOLD
        setPadding((14 * density).toInt(), (8 * density).toInt(), (14 * density).toInt(), (8 * density).toInt())
        val bg = GradientDrawable().apply {
          cornerRadius = 10 * density
          setColor(0xFFF59E0B.toInt())
        }
        background = bg
        setOnClickListener {
          hideTranslationCardOverlay()
          val intent = Intent(this@FloatingOverlayService, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra("REQUEST_SCREEN_CAPTURE_PERMISSION", true)
          }
          startActivity(intent)
        }
      }
      actionsRow.addView(grantBtn)
    } else if (!isLoading) {
      val copyBtn = TextView(this).apply {
        text = "📋 Salin"
        setTextColor(Color.WHITE)
        textSize = 12f
        typeface = android.graphics.Typeface.DEFAULT_BOLD
        setPadding((12 * density).toInt(), (6 * density).toInt(), (12 * density).toInt(), (6 * density).toInt())
        val bg = GradientDrawable().apply {
          cornerRadius = 8 * density
          setColor(0xFF0D9488.toInt())
        }
        background = bg
        setOnClickListener {
          val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
          val clip = ClipData.newPlainText("Terjemahan Layar", translated)
          clipboard.setPrimaryClip(clip)
          Toast.makeText(this@FloatingOverlayService, "Terjemahan disalin!", Toast.LENGTH_SHORT).show()
        }
      }
      actionsRow.addView(copyBtn)

      val spacer = View(this).apply {
        layoutParams = LinearLayout.LayoutParams((8 * density).toInt(), 1)
      }
      actionsRow.addView(spacer)

      val speakBtn = TextView(this).apply {
        text = "🔊 Suara"
        setTextColor(0xFF5EEAD4.toInt())
        textSize = 12f
        typeface = android.graphics.Typeface.DEFAULT_BOLD
        setPadding((12 * density).toInt(), (6 * density).toInt(), (12 * density).toInt(), (6 * density).toInt())
        val bg = GradientDrawable().apply {
          cornerRadius = 8 * density
          setColor(0xFF1E293B.toInt())
          setStroke((1 * density).toInt(), 0xFF14B8A6.toInt())
        }
        background = bg
        setOnClickListener {
          tts?.speak(translated, TextToSpeech.QUEUE_FLUSH, null, "service_tts")
        }
      }
      actionsRow.addView(speakBtn)
    }

    cardContainer.addView(actionsRow)

    translationCardOverlayView = cardContainer

    try {
      windowManager?.addView(translationCardOverlayView, cardParams)
      isOverlayCardVisible = true
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  private fun hideTranslationCardOverlay() {
    if (translationCardOverlayView != null) {
      try {
        windowManager?.removeView(translationCardOverlayView)
      } catch (e: Exception) {
        e.printStackTrace()
      }
      translationCardOverlayView = null
      isOverlayCardVisible = false
    }
    removeInPlaceBadges()
  }

  private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(
        CHANNEL_ID,
        "LayarTerjemah Tombol Apung",
        NotificationManager.IMPORTANCE_LOW
      ).apply {
        description = "Notifikasi status perekam layar dan tombol apung"
      }
      val manager = getSystemService(NotificationManager::class.java)
      manager.createNotificationChannel(channel)
    }
  }

  private fun createNotification(): Notification {
    val openIntent = Intent(this, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(
      this,
      0,
      openIntent,
      PendingIntent.FLAG_IMMUTABLE
    )

    return NotificationCompat.Builder(this, CHANNEL_ID)
      .setContentTitle("LayarTerjemah Aktif")
      .setContentText("Tombol apung siap memindai dan merekam teks layar asli")
      .setSmallIcon(android.R.drawable.ic_menu_agenda)
      .setContentIntent(pendingIntent)
      .setOngoing(true)
      .build()
  }

  override fun onDestroy() {
    super.onDestroy()
    isRunning = false
    serviceScope.cancel()
    hideTranslationCardOverlay()

    if (floatingBubbleView != null) {
      try {
        windowManager?.removeView(floatingBubbleView)
      } catch (e: Exception) {
        e.printStackTrace()
      }
      floatingBubbleView = null
    }

    cleanUpCaptureResources()

    captureThread?.quitSafely()
    captureThread = null
    captureHandler = null

    tts?.stop()
    tts?.shutdown()
  }
}
