package com.example.model

enum class EngineMode(val title: String, val subtitle: String) {
  GEMINI_LIVE("Gemini 3.5 Live Translate", "Model DeepMind khusus penerjemahan layar instan & real-time"),
  HYBRID("Hybrid (Rekomendasi)", "Kombinasi instan lokal + Gemini AI untuk akurasi tinggi"),
  OFFLINE_INSTANT("Hanya Offline", "Sangat cepat, tanpa kuota internet, kamus on-device")
}

data class TranslationBlock(
  val id: String,
  val originalText: String,
  val translatedText: String,
  val xRatio: Float = 0f,       // Normalized position 0.0 .. 1.0
  val yRatio: Float = 0f,       // Normalized position 0.0 .. 1.0
  val widthRatio: Float = 1f,   // Normalized width 0.0 .. 1.0
  val heightRatio: Float = 0.1f, // Normalized height 0.0 .. 1.0
  val confidence: Float = 0.95f,
  val sourceEngine: String = "Gemini 3.5 Live Translate",
  val category: String = "Layar Asli",
  val explanation: String = ""
)

data class RealCapturedScreen(
  val timestamp: Long = System.currentTimeMillis(),
  val fullOriginalText: String,
  val fullTranslatedText: String,
  val blocks: List<TranslationBlock>,
  val blockCount: Int = blocks.size
)

data class VocabularyItem(
  val id: String,
  val english: String,
  val indonesian: String,
  val category: String,
  val contextExample: String,
  val pronunciation: String
)
