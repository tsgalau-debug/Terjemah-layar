package com.example

import com.example.data.dictionary.LocalDictionary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun testLocalDictionaryExactGamingPhrases() {
    val translatedQuest = LocalDictionary.translateOffline("Main Quest")
    assertEquals("Misi utama", translatedQuest)

    val translatedBoss = LocalDictionary.translateOffline("Defeat the boss")
    assertEquals("Kalahkan bos", translatedBoss)

    val translatedInventory = LocalDictionary.translateOffline("Inventory is full")
    assertEquals("Inventaris penuh", translatedInventory)
  }

  @Test
  fun testLocalDictionarySystemPhrases() {
    val translatedOverlay = LocalDictionary.translateOffline("Draw over other apps")
    assertEquals("Tampilkan di atas aplikasi lain", translatedOverlay)

    val translatedInstant = LocalDictionary.translateOffline("Real-time instant translate")
    assertEquals("Terjemahan instan waktu-nyata", translatedInstant)
  }

  @Test
  fun testLocalDictionaryWordSubstitution() {
    val translatedSingle = LocalDictionary.translateOffline("start")
    assertEquals("mulai", translatedSingle)

    val translatedCapitalized = LocalDictionary.translateOffline("Start")
    assertEquals("Mulai", translatedCapitalized)

    val translatedSettings = LocalDictionary.translateOffline("Settings")
    assertEquals("Pengaturan", translatedSettings)
  }

  @Test
  fun testMorphologicalStemmingRegression() {
    // Regression check: words with plurals, past tense, and continuous forms
    // MUST NOT be repeated untouched in English.
    val pluralOptions = LocalDictionary.translateOffline("options")
    assertEquals("opsi", pluralOptions)

    val pluralEnemies = LocalDictionary.translateOffline("enemies")
    assertEquals("musuh", pluralEnemies)

    val pastStarted = LocalDictionary.translateOffline("started")
    assertEquals("telah mulai", pastStarted)

    val continuousPlaying = LocalDictionary.translateOffline("playing")
    assertEquals("sedang bermain", continuousPlaying)

    val adverbInstantly = LocalDictionary.translateOffline("instantly")
    assertEquals("secara instan", adverbInstantly)
  }

  @Test
  fun testVocabularyListIsPopulated() {
    val list = LocalDictionary.getVocabularyList()
    assertFalse(list.isEmpty())
    assertTrue(list.any { it.english == "Critical Hit" })
    assertTrue(list.any { it.english == "Overlay" })
  }

  @Test
  fun testExactPhraseWithPunctuationPreservationRegression() {
    // Regression check: Phrases with punctuation like ! or . must still match exact phrases
    val translatedWithExclamation = LocalDictionary.translateOffline("Game Over!")
    assertEquals("Permainan berakhir!", translatedWithExclamation)

    val translatedWithPeriod = LocalDictionary.translateOffline("Quest completed.")
    assertEquals("Misi selesai.", translatedWithPeriod)
  }

  @Test
  fun testPartialPhraseSubstitutionContinuesSentenceTranslationRegression() {
    // Regression check: When a phrase like "sign in" is substituted, the remaining English
    // words in the sentence (like "please", "to", "play") MUST still be translated.
    val translatedSentence = LocalDictionary.translateOffline("Please sign in to play")
    assertTrue("Should translate sign in to Masuk", translatedSentence.contains("Masuk", ignoreCase = true))
    assertTrue("Should translate please to silakan", translatedSentence.contains("silakan", ignoreCase = true))
    assertTrue("Should translate play to bermain", translatedSentence.contains("bermain", ignoreCase = true))
    assertFalse("Should not leave English please untouched", translatedSentence.startsWith("Please sign"))
  }

  @Test
  fun testGemini35LiveTranslateModelIsActiveAndConfigured() {
    // Validasi ketat: Model Live API aktif dengan format resmi preview
    assertEquals("gemini-3.5-live-translate-preview", com.example.data.remote.GeminiTranslatorClient.MODEL_LIVE_TRANSLATE)
    assertEquals("gemini-3.5-live-translate-preview", com.example.data.remote.GeminiTranslatorClient.activePrimaryModel)

    // Validasi mode mesin penerjemahan
    assertEquals("Gemini 3.5 Live Translate", com.example.model.EngineMode.GEMINI_LIVE.title)

    // Validasi protokol WebSocket Live API
    assertEquals(
      "wss://generativelanguage.googleapis.com/ws/google.ai.generativelanguage.v1beta.GenerativeService.BidiGenerateContent",
      com.example.data.remote.GeminiTranslatorClient.WS_BASE_URL
    )
  }

  @Test
  fun testLocalDictionaryEmptyAndEdgeCases() {
    assertEquals("", LocalDictionary.translateOffline(""))
    assertEquals("", LocalDictionary.translateOffline("   "))
    assertEquals("...", LocalDictionary.translateOffline("..."))
  }

  @Test
  fun testLocalDictionaryMixedSentenceAndUnknownWords() {
    val translated = LocalDictionary.translateOffline("You have 3 legendary weapons")
    assertTrue(translated.contains("Anda", ignoreCase = true))
    assertTrue(translated.contains("memiliki", ignoreCase = true))
    assertTrue(translated.contains("3"))
    assertTrue(translated.contains("senjata", ignoreCase = true))
    assertTrue(translated.contains("legendaris", ignoreCase = true))
  }

  @Test
  fun testVocabularyCategoriesIntegrity() {
    val list = LocalDictionary.getVocabularyList()
    val categories = list.map { it.category }.distinct()
    assertTrue(categories.contains("Game"))
    assertTrue(categories.contains("Sistem"))
    assertTrue(categories.contains("Teknologi"))
    assertTrue(list.all { it.english.isNotEmpty() && it.indonesian.isNotEmpty() && it.pronunciation.isNotEmpty() })
  }
}

