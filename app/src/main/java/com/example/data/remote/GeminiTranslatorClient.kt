package com.example.data.remote

import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

object GeminiTranslatorClient {
  private const val TAG = "GeminiTranslator"
  // =========================================================================
  // PERINGATAN KERAS / STRICT RULE:
  // Model resmi Google AI Studio Live API menggunakan akhiran '-preview'.
  // Model utama: gemini-3.5-live-translate-preview (Gemini 3.5 Live Translate)
  // Model multimodal live: gemini-3.1-flash-live-preview (Gemini 3 Flash Live)
  // DIHUBUNGKAN VIA PROTOKOL RESMI: Live API WebSocket (BidiGenerateContent).
  // =========================================================================
  const val MODEL_LIVE_TRANSLATE = "gemini-3.5-live-translate-preview"
  const val MODEL_FLASH_LIVE = "gemini-3.1-flash-live-preview"
  const val MODEL_TTS = "gemini-2.5-flash-preview-tts"
  const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"
  const val WS_BASE_URL = "wss://generativelanguage.googleapis.com/ws/google.ai.generativelanguage.v1beta.GenerativeService.BidiGenerateContent"

  val activePrimaryModel: String = MODEL_LIVE_TRANSLATE

  private const val PREFS_NAME = "layar_terjemah_prefs"
  private const val KEY_CUSTOM_API_KEY = "custom_gemini_api_key"

  @Volatile
  private var customApiKey: String = ""

  private val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .build()

  fun init(context: android.content.Context) {
    val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE)
    customApiKey = prefs.getString(KEY_CUSTOM_API_KEY, "")?.trim() ?: ""
  }

  fun setCustomApiKey(context: android.content.Context?, apiKey: String) {
    val cleaned = apiKey.trim()
    customApiKey = cleaned
    context?.applicationContext
      ?.getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE)
      ?.edit()
      ?.putString(KEY_CUSTOM_API_KEY, cleaned)
      ?.apply()
  }

  fun getCustomApiKey(): String = customApiKey

  val activeApiKey: String
    get() {
      if (customApiKey.isNotBlank()) return customApiKey
      val buildKey = BuildConfig.GEMINI_API_KEY.trim()
      return if (buildKey.isNotBlank() && buildKey != "MY_GEMINI_API_KEY") buildKey else ""
    }

  val isApiKeyConfigured: Boolean
    get() = activeApiKey.isNotBlank()

  /**
   * Menerjemahkan teks secara eksklusif menggunakan Gemini Live API WebSocket (BidiGenerateContent).
   * Menampilkan hasil asli dan status koneksi live secara transparan.
   */
  suspend fun translateText(englishText: String): String = withContext(Dispatchers.IO) {
    val clean = englishText.trim()
    if (clean.isEmpty()) return@withContext ""

    val apiKey = activeApiKey
    if (apiKey.isBlank()) {
      return@withContext "[Error Gemini Live API]: API Key belum disetel. Masukkan API Key di menu Pengaturan aplikasi atau Secrets."
    }

    // Panggil murni via Gemini Live API WebSocket (BidiGenerateContent).
    // TIDAK ADA BYPASS ATAU MODEL REST LAIN (PROSES DEBUG ASLI).
    try {
      withTimeout(15000L) {
        translateViaLiveWebSocket(clean, MODEL_LIVE_TRANSLATE, apiKey)
      }
    } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
      "[Gemini Live API Timeout]: WebSocket tidak menerima respon dalam 15 detik."
    } catch (e: Exception) {
      "[Gemini Live API Exception]: ${e.javaClass.simpleName}: ${e.message}"
    }
  }

  /**
   * Menghubungkan langsung ke Google Live API melalui WebSocket (BidiGenerateContent).
   */
  private suspend fun translateViaLiveWebSocket(
    englishText: String,
    modelName: String,
    apiKey: String
  ): String = withContext(Dispatchers.IO) {
    suspendCancellableCoroutine { continuation ->
      val wsUrl = "$WS_BASE_URL?key=$apiKey"
      val request = Request.Builder().url(wsUrl).build()

      val accumulatedResponse = StringBuilder()
      var isResumed = false
      var currentWebSocket: WebSocket? = null

      fun safeResume(result: String) {
        if (!isResumed) {
          isResumed = true
          try {
            currentWebSocket?.close(1000, "Done")
          } catch (_: Exception) {}
          continuation.resume(result)
        }
      }

      fun handleIncomingFrame(webSocket: WebSocket, rawFrame: String) {
        try {
          val json = JSONObject(rawFrame)

          // Setup selesai, kirim giliran teks bahasa Inggris asli dari client
          if (json.has("setupComplete")) {
            Log.d(TAG, "Live API setupComplete received, sending clientContent...")
            val contentJson = buildClientContentFrame(englishText)
            webSocket.send(contentJson.toString())
            return
          }

          // Periksa jika ada error frame dari server
          if (json.has("error")) {
            val errObj = json.optJSONObject("error")
            val errMsg = errObj?.optString("message") ?: json.optString("error")
            safeResume("[Gemini Live API Server Error]: $errMsg")
            return
          }

          // Menerima serverContent dari model
          val serverContent = json.optJSONObject("serverContent")
          if (serverContent != null) {
            val modelTurn = serverContent.optJSONObject("modelTurn")
            val parts = modelTurn?.optJSONArray("parts")
            if (parts != null) {
              for (i in 0 until parts.length()) {
                val part = parts.optJSONObject(i)
                val textPart = part?.optString("text", "") ?: ""
                if (textPart.isNotEmpty()) {
                  accumulatedResponse.append(textPart)
                }
              }
            }

            val turnComplete = serverContent.optBoolean("turnComplete", false)
            if (turnComplete) {
              val finalOutput = accumulatedResponse.toString().trim()
              if (finalOutput.isNotEmpty()) {
                safeResume(finalOutput)
              }
            }
          }
        } catch (e: Exception) {
          Log.e(TAG, "Error parsing Live API WebSocket frame: ${e.message}", e)
        }
      }

      val listener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
          currentWebSocket = webSocket
          Log.d(TAG, "Gemini Live API WebSocket connected.")

          // 1. Kirim Setup frame
          val setupJson = buildSetupFrame(modelName)
          webSocket.send(setupJson.toString())
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
          handleIncomingFrame(webSocket, text)
        }

        override fun onMessage(webSocket: WebSocket, bytes: okio.ByteString) {
          handleIncomingFrame(webSocket, bytes.utf8())
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
          val isSocketClosed = t is java.net.SocketException ||
              t.message?.contains("Socket closed", ignoreCase = true) == true ||
              t.message?.contains("Software caused connection abort", ignoreCase = true) == true

          if (isResumed || isSocketClosed) {
            Log.d(TAG, "WebSocket stream disconnected: ${t.message}")
            return
          }

          val code = response?.code
          val msg = response?.message ?: t.message ?: "Unknown WebSocket Error"
          Log.w(TAG, "Live API WebSocket closed/failed: HTTP $code - $msg")
          val errorText = if (code != null) {
            "[Gemini Live API HTTP $code]: $msg"
          } else {
            "[Gemini Live API Error]: ${t.javaClass.simpleName}: $msg"
          }
          safeResume(errorText)
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
          Log.d(TAG, "Live API WebSocket onClosing: $code $reason")
          val currentText = accumulatedResponse.toString().trim()
          if (currentText.isNotEmpty()) {
            safeResume(currentText)
          } else if (code != 1000) {
            safeResume("[Gemini Live API Closed $code]: $reason")
          }
        }
      }

      val ws = okHttpClient.newWebSocket(request, listener)
      currentWebSocket = ws

      continuation.invokeOnCancellation {
        isResumed = true
        try {
          ws.cancel()
        } catch (_: Exception) {}
      }
    }
  }

  internal fun buildSetupFrame(modelName: String): JSONObject {
    val modelWithPrefix = if (modelName.startsWith("models/")) modelName else "models/$modelName"
    return JSONObject().apply {
      put("setup", JSONObject().apply {
        put("model", modelWithPrefix)
        put("generationConfig", JSONObject().apply {
          put("responseModalities", JSONArray().apply { put("TEXT") })
          put("temperature", 0.2)
        })
        put("systemInstruction", JSONObject().apply {
          put("parts", JSONArray().apply {
            put(JSONObject().apply {
              put("text", "You are Gemini 3.5 Live Translate, specialized real-time screen and UI translator. Translate on-screen English text into natural, fluent Bahasa Indonesia, accurately preserving gaming, UI, and contextual phrasing. Return ONLY the translation, with no explanation or extra quotes.")
            })
          })
        })
      })
    }
  }

  internal fun buildClientContentFrame(englishText: String): JSONObject {
    return JSONObject().apply {
      put("clientContent", JSONObject().apply {
        put("turns", JSONArray().apply {
          put(JSONObject().apply {
            put("role", "user")
            put("parts", JSONArray().apply {
              put(JSONObject().apply {
                put("text", "Translate to Indonesian: $englishText")
              })
            })
          })
        })
        put("turnComplete", true)
      })
    }
  }

  /**
   * Generates natural human-like voice audio using Gemini TTS (DeepMind model series).
   * Supports prebuilt voices: "Kore", "Puck", "Fenrir", "Aoede".
   */
  suspend fun generateSpeechAudio(text: String, voiceName: String = "Kore"): ByteArray? = withContext(Dispatchers.IO) {
    val clean = text.trim()
    if (clean.isEmpty()) return@withContext null

    val apiKey = activeApiKey
    if (apiKey.isBlank()) {
      return@withContext null
    }

    try {
      val endpoint = "$BASE_URL/$MODEL_TTS:generateContent?key=$apiKey"
      val requestJson = JSONObject().apply {
        put("contents", JSONArray().apply {
          put(JSONObject().apply {
            put("parts", JSONArray().apply {
              put(JSONObject().apply {
                put("text", clean)
              })
            })
          })
        })
        put("generationConfig", JSONObject().apply {
          put("responseModalities", JSONArray().apply {
            put("AUDIO")
          })
          put("speechConfig", JSONObject().apply {
            put("voiceConfig", JSONObject().apply {
              put("prebuiltVoiceConfig", JSONObject().apply {
                put("voiceName", voiceName)
              })
            })
          })
        })
      }

      val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())
      val request = Request.Builder()
        .url(endpoint)
        .post(requestBody)
        .build()

      val response = okHttpClient.newCall(request).execute()
      val responseString = response.body?.string() ?: ""

      if (!response.isSuccessful) {
        Log.w(TAG, "Gemini Audio call failed: ${response.code} $responseString")
        return@withContext null
      }

      val rootJson = JSONObject(responseString)
      val candidates = rootJson.optJSONArray("candidates")
      if (candidates != null && candidates.length() > 0) {
        val parts = candidates.getJSONObject(0).optJSONObject("content")?.optJSONArray("parts")
        if (parts != null) {
          for (i in 0 until parts.length()) {
            val part = parts.optJSONObject(i) ?: continue
            val inlineData = part.optJSONObject("inlineData")
            val base64Data = inlineData?.optString("data", "") ?: ""
            if (base64Data.isNotEmpty()) {
              return@withContext Base64.decode(base64Data, Base64.DEFAULT)
            }
          }
        }
      }
      null
    } catch (e: Exception) {
      Log.e(TAG, "Error generating audio via Gemini TTS: ${e.message}", e)
      null
    }
  }
}
