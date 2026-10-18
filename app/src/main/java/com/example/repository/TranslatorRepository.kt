package com.example.repository

import com.example.data.dictionary.LocalDictionary
import com.example.data.local.TranslationDao
import com.example.data.local.TranslationEntity
import com.example.data.remote.GeminiTranslatorClient
import com.example.model.EngineMode
import com.example.model.TranslationBlock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class TranslatorRepository(
  private val dao: TranslationDao
) {

  val historyFlow: Flow<List<TranslationEntity>> = dao.getAllHistory()
  val starredFlow: Flow<List<TranslationEntity>> = dao.getStarredTranslations()

  suspend fun translateText(text: String, mode: EngineMode): String = withContext(Dispatchers.IO) {
    when (mode) {
      EngineMode.OFFLINE_INSTANT -> LocalDictionary.translateOffline(text)
      EngineMode.GEMINI_LIVE -> GeminiTranslatorClient.translateText(text)
      EngineMode.HYBRID -> {
        if (GeminiTranslatorClient.isApiKeyConfigured) {
          val liveResult = GeminiTranslatorClient.translateText(text)
          if (liveResult.isNotBlank() && !liveResult.startsWith("[")) {
            liveResult
          } else {
            LocalDictionary.translateOffline(text)
          }
        } else {
          LocalDictionary.translateOffline(text)
        }
      }
    }
  }

  suspend fun translateBlocks(blocks: List<TranslationBlock>, mode: EngineMode): List<TranslationBlock> = withContext(Dispatchers.IO) {
    blocks.map { block ->
      val translated = translateText(block.originalText, mode)
      val engineLabel = when (mode) {
        EngineMode.OFFLINE_INSTANT -> "Offline Lexicon"
        EngineMode.GEMINI_LIVE -> "Gemini 3.5 Live Translate"
        EngineMode.HYBRID -> if (GeminiTranslatorClient.isApiKeyConfigured) "Hybrid (Gemini Live)" else "Hybrid (Offline)"
      }
      block.copy(
        translatedText = translated,
        sourceEngine = engineLabel
      )
    }
  }

  suspend fun saveToHistory(
    original: String,
    translated: String,
    sourceContext: String = "Layar Asli",
    engine: String = "Gemini 3.5 Live Translate",
    count: Int = 1
  ): Long {
    return dao.insert(
      TranslationEntity(
        originalText = original,
        translatedText = translated,
        sourceContext = sourceContext,
        engineUsed = engine,
        blockCount = count
      )
    )
  }

  suspend fun toggleStarred(entity: TranslationEntity) {
    dao.update(entity.copy(isStarred = !entity.isStarred))
  }

  suspend fun deleteHistoryItem(entity: TranslationEntity) {
    dao.delete(entity)
  }

  suspend fun clearAllHistory() {
    dao.clearAll()
  }
}
