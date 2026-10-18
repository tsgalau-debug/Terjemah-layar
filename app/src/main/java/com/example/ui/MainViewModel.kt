package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.dictionary.LocalDictionary
import com.example.data.local.AppDatabase
import com.example.data.local.TranslationEntity
import com.example.data.remote.GeminiTranslatorClient
import com.example.model.EngineMode
import com.example.model.VocabularyItem
import com.example.repository.TranslatorRepository
import com.example.service.FloatingOverlayService
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import android.os.Build
import com.example.model.TranslationBlock
import java.io.File
import java.util.Locale

enum class ScreenTab {
  DASHBOARD,
  SANDBOX,
  HISTORY,
  VOCABULARY,
  SETTINGS
}

data class MainUiState(
  val currentTab: ScreenTab = ScreenTab.DASHBOARD,
  val isServiceRunning: Boolean = false,
  val hasOverlayPermission: Boolean = false,
  val isScreenCaptureReady: Boolean = false,
  val engineMode: EngineMode = EngineMode.HYBRID,
  val overlayOpacity: Float = 0.88f,
  val vocabularyList: List<VocabularyItem> = emptyList(),
  val customApiKey: String = "",
  val isGeminiApiKeyConfigured: Boolean = false,
  val isProcessingImage: Boolean = false,
  val useGeminiAiAudio: Boolean = false,
  val selectedGeminiVoice: String = "Kore",
  val lastCapturedOriginal: String? = null,
  val lastCapturedTranslated: String? = null,
  val selectedSandboxScenario: String = "Game RPG",
  val isSandboxScanning: Boolean = false,
  val sandboxBlocks: List<TranslationBlock> = emptyList(),
  val selectedSandboxBlock: TranslationBlock? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: TranslatorRepository
  private var tts: TextToSpeech? = null
  private var isTtsReady = false
  private var activeMediaPlayer: MediaPlayer? = null

  private val _uiState = MutableStateFlow(MainUiState())
  val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

  val historyList: StateFlow<List<TranslationEntity>>
  val starredList: StateFlow<List<TranslationEntity>>

  init {
    GeminiTranslatorClient.init(application)
    val database = AppDatabase.getDatabase(application)
    repository = TranslatorRepository(database.translationDao())

    historyList = repository.historyFlow.stateIn(
      viewModelScope,
      SharingStarted.WhileSubscribed(5000),
      emptyList()
    )

    starredList = repository.starredFlow.stateIn(
      viewModelScope,
      SharingStarted.WhileSubscribed(5000),
      emptyList()
    )

    _uiState.value = _uiState.value.copy(
      vocabularyList = LocalDictionary.getVocabularyList(),
      customApiKey = GeminiTranslatorClient.getCustomApiKey(),
      isGeminiApiKeyConfigured = GeminiTranslatorClient.isApiKeyConfigured,
      hasOverlayPermission = Settings.canDrawOverlays(application),
      isScreenCaptureReady = FloatingOverlayService.isScreenCaptureReady
    )

    initTts(application)
  }

  fun saveCustomApiKey(apiKey: String) {
    val context = getApplication<Application>()
    GeminiTranslatorClient.setCustomApiKey(context, apiKey)
    _uiState.value = _uiState.value.copy(
      customApiKey = GeminiTranslatorClient.getCustomApiKey(),
      isGeminiApiKeyConfigured = GeminiTranslatorClient.isApiKeyConfigured
    )
  }

  fun clearCustomApiKey() {
    val context = getApplication<Application>()
    GeminiTranslatorClient.setCustomApiKey(context, "")
    _uiState.value = _uiState.value.copy(
      customApiKey = "",
      isGeminiApiKeyConfigured = GeminiTranslatorClient.isApiKeyConfigured
    )
  }

  private fun initTts(context: Context) {
    tts = TextToSpeech(context) { status ->
      if (status == TextToSpeech.SUCCESS) {
        tts?.language = Locale.forLanguageTag("id-ID")
        isTtsReady = true
      }
    }
  }

  fun checkPermissions() {
    val context = getApplication<Application>()
    val hasOverlay = Settings.canDrawOverlays(context)
    val isRunning = FloatingOverlayService.isRunning
    val isCaptureReady = FloatingOverlayService.isScreenCaptureReady

    _uiState.value = _uiState.value.copy(
      hasOverlayPermission = hasOverlay,
      isServiceRunning = isRunning,
      isScreenCaptureReady = isCaptureReady,
      customApiKey = GeminiTranslatorClient.getCustomApiKey(),
      isGeminiApiKeyConfigured = GeminiTranslatorClient.isApiKeyConfigured
    )
  }

  fun onScreenCapturePermissionGranted(resultCode: Int, data: Intent?) {
    FloatingOverlayService.setProjectionData(resultCode, data)
    val context = getApplication<Application>()
    val intent = Intent(context, FloatingOverlayService::class.java).apply {
      action = FloatingOverlayService.ACTION_UPDATE_PROJECTION
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      context.startForegroundService(intent)
    } else {
      context.startService(intent)
    }
    _uiState.value = _uiState.value.copy(isScreenCaptureReady = true)
    Toast.makeText(context, "Izin rekam layar aktif!", Toast.LENGTH_SHORT).show()
  }

  fun processScreenshotBitmap(bitmap: Bitmap) {
    _uiState.value = _uiState.value.copy(isProcessingImage = true)
    viewModelScope.launch {
      try {
        val inputImage = InputImage.fromBitmap(bitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        recognizer.process(inputImage)
          .addOnSuccessListener { visionText ->
            val detected = visionText.text.trim()
            if (detected.isEmpty()) {
              _uiState.value = _uiState.value.copy(
                isProcessingImage = false,
                lastCapturedOriginal = "Tidak ada teks terdeteksi di gambar",
                lastCapturedTranslated = "Gambar screenshot tidak mengandung teks bahasa Inggris yang terbaca."
              )
            } else {
              viewModelScope.launch {
                val translated = repository.translateText(detected, _uiState.value.engineMode)
                _uiState.value = _uiState.value.copy(
                  isProcessingImage = false,
                  lastCapturedOriginal = detected,
                  lastCapturedTranslated = translated
                )
                repository.saveToHistory(
                  original = detected,
                  translated = translated,
                  sourceContext = "Tangkapan Layar Galeri",
                  engine = when (_uiState.value.engineMode) {
                    EngineMode.GEMINI_LIVE -> "Gemini 3.5 Live Translate"
                    EngineMode.HYBRID -> "Hybrid Engine"
                    EngineMode.OFFLINE_INSTANT -> "Offline Lexicon"
                  },
                  count = visionText.textBlocks.size
                )
              }
            }
          }
          .addOnFailureListener { e ->
            _uiState.value = _uiState.value.copy(
              isProcessingImage = false,
              lastCapturedOriginal = "Gagal memproses gambar",
              lastCapturedTranslated = "Error OCR: ${e.message}"
            )
          }
          .addOnCompleteListener {
            recognizer.close()
          }
      } catch (e: Exception) {
        _uiState.value = _uiState.value.copy(
          isProcessingImage = false,
          lastCapturedOriginal = "Gagal memproses gambar",
          lastCapturedTranslated = "Error: ${e.message}"
        )
      }
    }
  }

  fun setTab(tab: ScreenTab) {
    _uiState.value = _uiState.value.copy(currentTab = tab)
  }

  fun setEngineMode(mode: EngineMode) {
    _uiState.value = _uiState.value.copy(engineMode = mode)
  }

  fun setOverlayOpacity(opacity: Float) {
    _uiState.value = _uiState.value.copy(overlayOpacity = opacity)
  }

  fun setUseGeminiAudio(use: Boolean) {
    _uiState.value = _uiState.value.copy(useGeminiAiAudio = use)
  }

  fun setGeminiVoice(voice: String) {
    _uiState.value = _uiState.value.copy(selectedGeminiVoice = voice)
  }

  fun toggleFloatingService() {
    val context = getApplication<Application>()
    if (_uiState.value.isServiceRunning) {
      FloatingOverlayService.stopService(context)
      _uiState.value = _uiState.value.copy(isServiceRunning = false)
      Toast.makeText(context, "Tombol apung dinonaktifkan", Toast.LENGTH_SHORT).show()
    } else {
      if (!Settings.canDrawOverlays(context)) {
        FloatingOverlayService.startService(context)
      } else {
        FloatingOverlayService.startService(context)
        _uiState.value = _uiState.value.copy(isServiceRunning = true)
        Toast.makeText(context, "Tombol apung LayarTerjemah aktif!", Toast.LENGTH_SHORT).show()
      }
    }
  }

  fun speakEnglish(text: String) {
    if (_uiState.value.useGeminiAiAudio && _uiState.value.isGeminiApiKeyConfigured) {
      playGeminiAudio(text, isEnglish = true)
    } else {
      if (isTtsReady) {
        tts?.language = Locale.ENGLISH
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tts_en")
      }
    }
  }

  fun speakIndonesian(text: String) {
    if (_uiState.value.useGeminiAiAudio && _uiState.value.isGeminiApiKeyConfigured) {
      playGeminiAudio(text, isEnglish = false)
    } else {
      if (isTtsReady) {
        val idLocale = Locale.forLanguageTag("id-ID")
        tts?.language = idLocale
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tts_id")
      }
    }
  }

  private fun playGeminiAudio(text: String, isEnglish: Boolean = false) {
    viewModelScope.launch {
      val audioBytes = GeminiTranslatorClient.generateSpeechAudio(text, _uiState.value.selectedGeminiVoice)
      if (audioBytes != null) {
        try {
          activeMediaPlayer?.release()
          val tempFile = File.createTempFile("gemini_tts", ".wav", getApplication<Application>().cacheDir)
          tempFile.writeBytes(audioBytes)
          val player = MediaPlayer().apply {
            setDataSource(tempFile.absolutePath)
            prepare()
            start()
            setOnCompletionListener {
              tempFile.delete()
              release()
            }
          }
          activeMediaPlayer = player
        } catch (e: Exception) {
          fallbackAndroidTts(text, isEnglish)
        }
      } else {
        fallbackAndroidTts(text, isEnglish)
      }
    }
  }

  private fun fallbackAndroidTts(text: String, isEnglish: Boolean = false) {
    if (isTtsReady) {
      tts?.language = if (isEnglish) Locale.ENGLISH else Locale.forLanguageTag("id-ID")
      tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tts_fallback")
    }
  }

  fun selectSandboxScenario(scenario: String) {
    _uiState.value = _uiState.value.copy(
      selectedSandboxScenario = scenario,
      sandboxBlocks = emptyList(),
      selectedSandboxBlock = null
    )
  }

  fun selectSandboxBlock(block: TranslationBlock?) {
    _uiState.value = _uiState.value.copy(selectedSandboxBlock = block)
  }

  fun triggerSandboxScan() {
    _uiState.value = _uiState.value.copy(isSandboxScanning = true)
    viewModelScope.launch {
      kotlinx.coroutines.delay(450)

      val scenario = _uiState.value.selectedSandboxScenario
      val engineLabel = when (_uiState.value.engineMode) {
        EngineMode.GEMINI_LIVE -> "Gemini 3.5 Live Translate"
        EngineMode.HYBRID -> if (GeminiTranslatorClient.isApiKeyConfigured) "Hybrid (Gemini AI)" else "Hybrid (Offline)"
        EngineMode.OFFLINE_INSTANT -> "Offline Lexicon"
      }

      val generatedBlocks = when (scenario) {
        "Game RPG" -> listOf(
          TranslationBlock(
            id = "b1",
            originalText = "Quest: Defeat the Shadow Dragon in the ancient dungeon.",
            translatedText = repository.translateText("Quest: Defeat the Shadow Dragon in the ancient dungeon.", _uiState.value.engineMode),
            xRatio = 0.05f,
            yRatio = 0.12f,
            widthRatio = 0.90f,
            heightRatio = 0.10f,
            sourceEngine = engineLabel,
            category = "Game Quest"
          ),
          TranslationBlock(
            id = "b2",
            originalText = "Critical Hit! 2,450 damage dealt to enemy.",
            translatedText = repository.translateText("Critical Hit! 2,450 damage dealt to enemy.", _uiState.value.engineMode),
            xRatio = 0.08f,
            yRatio = 0.35f,
            widthRatio = 0.84f,
            heightRatio = 0.08f,
            sourceEngine = engineLabel,
            category = "Battle Log"
          ),
          TranslationBlock(
            id = "b3",
            originalText = "Inventory is full. Equip your legendary weapon.",
            translatedText = repository.translateText("Inventory is full. Equip your legendary weapon.", _uiState.value.engineMode),
            xRatio = 0.05f,
            yRatio = 0.58f,
            widthRatio = 0.90f,
            heightRatio = 0.09f,
            sourceEngine = engineLabel,
            category = "Equipment"
          )
        )
        "Artikel Tech" -> listOf(
          TranslationBlock(
            id = "b1",
            originalText = "On-Device Neural Engine accelerates real-time text processing.",
            translatedText = repository.translateText("On-Device Neural Engine accelerates real-time text processing.", _uiState.value.engineMode),
            xRatio = 0.05f,
            yRatio = 0.15f,
            widthRatio = 0.90f,
            heightRatio = 0.10f,
            sourceEngine = engineLabel,
            category = "Teknologi"
          ),
          TranslationBlock(
            id = "b2",
            originalText = "Privacy policy: Screen capture permissions remain strictly on-device.",
            translatedText = repository.translateText("Privacy policy: Screen capture permissions remain strictly on-device.", _uiState.value.engineMode),
            xRatio = 0.05f,
            yRatio = 0.42f,
            widthRatio = 0.90f,
            heightRatio = 0.10f,
            sourceEngine = engineLabel,
            category = "Keamanan"
          )
        )
        "Komik / Webtoon" -> listOf(
          TranslationBlock(
            id = "b1",
            originalText = "Wait! Don't go there alone, the monster is dangerous!",
            translatedText = repository.translateText("Wait! Don't go there alone, the monster is dangerous!", _uiState.value.engineMode),
            xRatio = 0.05f,
            yRatio = 0.18f,
            widthRatio = 0.90f,
            heightRatio = 0.10f,
            sourceEngine = engineLabel,
            category = "Dialog"
          ),
          TranslationBlock(
            id = "b2",
            originalText = "I will protect everyone no matter what happens.",
            translatedText = repository.translateText("I will protect everyone no matter what happens.", _uiState.value.engineMode),
            xRatio = 0.05f,
            yRatio = 0.50f,
            widthRatio = 0.90f,
            heightRatio = 0.10f,
            sourceEngine = engineLabel,
            category = "Dialog"
          )
        )
        else -> listOf(
          TranslationBlock(
            id = "b1",
            originalText = "Hey, are you ready for the ranked match tonight?",
            translatedText = repository.translateText("Hey, are you ready for the ranked match tonight?", _uiState.value.engineMode),
            xRatio = 0.05f,
            yRatio = 0.16f,
            widthRatio = 0.90f,
            heightRatio = 0.09f,
            sourceEngine = engineLabel,
            category = "Chat"
          ),
          TranslationBlock(
            id = "b2",
            originalText = "Let's join squad and win the battle together!",
            translatedText = repository.translateText("Let's join squad and win the battle together!", _uiState.value.engineMode),
            xRatio = 0.05f,
            yRatio = 0.42f,
            widthRatio = 0.90f,
            heightRatio = 0.09f,
            sourceEngine = engineLabel,
            category = "Chat"
          )
        )
      }

      _uiState.value = _uiState.value.copy(
        isSandboxScanning = false,
        sandboxBlocks = generatedBlocks,
        selectedSandboxBlock = generatedBlocks.firstOrNull()
      )

      // Save summary to history
      val combinedOriginal = generatedBlocks.joinToString("\n") { it.originalText }
      val combinedTranslated = generatedBlocks.joinToString("\n") { it.translatedText }
      repository.saveToHistory(
        original = combinedOriginal,
        translated = combinedTranslated,
        sourceContext = "Arena Uji ($scenario)",
        engine = engineLabel,
        count = generatedBlocks.size
      )
    }
  }

  fun toggleStarredHistory(entity: TranslationEntity) {
    viewModelScope.launch {
      repository.toggleStarred(entity)
    }
  }

  fun deleteHistoryItem(entity: TranslationEntity) {
    viewModelScope.launch {
      repository.deleteHistoryItem(entity)
    }
  }

  fun clearAllHistory() {
    viewModelScope.launch {
      repository.clearAllHistory()
    }
  }

  override fun onCleared() {
    super.onCleared()
    activeMediaPlayer?.release()
    tts?.stop()
    tts?.shutdown()
  }
}
