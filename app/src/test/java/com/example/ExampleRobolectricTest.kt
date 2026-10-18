package com.example

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.TranslationDao
import com.example.data.local.TranslationEntity
import com.example.model.EngineMode
import com.example.repository.TranslatorRepository
import com.example.ui.MainViewModel
import com.example.ui.ScreenTab
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  private lateinit var database: AppDatabase
  private lateinit var dao: TranslationDao
  private lateinit var repository: TranslatorRepository

  @Before
  fun setup() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    dao = database.translationDao()
    repository = TranslatorRepository(dao)
  }

  @After
  fun tearDown() {
    database.close()
  }

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("LayarTerjemah", appName)
  }

  @Test
  fun `room database crud operations via repository`() = runTest {
    // 1. Insert
    val id = repository.saveToHistory(
      original = "Critical Hit!",
      translated = "Serangan Telak!",
      sourceContext = "Arena Uji (Game RPG)",
      engine = "Gemini 3.5 Live Translate",
      count = 1
    )
    assertTrue(id > 0)

    // 2. Query
    val history = repository.historyFlow.first()
    assertEquals(1, history.size)
    val item = history[0]
    assertEquals("Critical Hit!", item.originalText)
    assertEquals("Serangan Telak!", item.translatedText)
    assertFalse(item.isStarred)

    // 3. Toggle Starred
    repository.toggleStarred(item)
    val starred = repository.starredFlow.first()
    assertEquals(1, starred.size)
    assertTrue(starred[0].isStarred)

    // 4. Delete item
    repository.deleteHistoryItem(starred[0])
    val afterDelete = repository.historyFlow.first()
    assertTrue(afterDelete.isEmpty())
  }

  @Test
  fun `room clear all history removes all entries`() = runTest {
    repository.saveToHistory("Quest 1", "Misi 1")
    repository.saveToHistory("Quest 2", "Misi 2")
    assertEquals(2, repository.historyFlow.first().size)

    repository.clearAllHistory()
    assertEquals(0, repository.historyFlow.first().size)
  }

  @Test
  fun `hybrid and offline translation modes return valid Indonesian translations`() = runTest {
    val offlineResult = repository.translateText("Inventory is full", EngineMode.OFFLINE_INSTANT)
    assertEquals("Inventaris penuh", offlineResult)

    val hybridResult = repository.translateText("Main Quest", EngineMode.HYBRID)
    assertTrue(hybridResult.isNotBlank())
    assertFalse(hybridResult.startsWith("[Error"))
  }

  @Test
  fun `main view model state management and navigation`() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = MainViewModel(app)

    val initialState = viewModel.uiState.value
    assertEquals(ScreenTab.DASHBOARD, initialState.currentTab)
    assertEquals(EngineMode.HYBRID, initialState.engineMode)
    assertTrue(initialState.vocabularyList.isNotEmpty())

    // Tab change
    viewModel.setTab(ScreenTab.SANDBOX)
    assertEquals(ScreenTab.SANDBOX, viewModel.uiState.value.currentTab)

    viewModel.setTab(ScreenTab.HISTORY)
    assertEquals(ScreenTab.HISTORY, viewModel.uiState.value.currentTab)

    // Engine mode change
    viewModel.setEngineMode(EngineMode.OFFLINE_INSTANT)
    assertEquals(EngineMode.OFFLINE_INSTANT, viewModel.uiState.value.currentTab.let { viewModel.uiState.value.engineMode })

    // Overlay opacity change
    viewModel.setOverlayOpacity(0.95f)
    assertEquals(0.95f, viewModel.uiState.value.overlayOpacity)

    // Scenario selection
    viewModel.selectSandboxScenario("Komik / Webtoon")
    assertEquals("Komik / Webtoon", viewModel.uiState.value.selectedSandboxScenario)
  }

  @Test
  fun `live api websocket frame construction prevents variable shadowing regression`() {
    val englishInput = "Defeat the Shadow Dragon in the ancient dungeon."
    val clientFrame = com.example.data.remote.GeminiTranslatorClient.buildClientContentFrame(englishInput)
    val frameStr = clientFrame.toString()

    assertTrue(frameStr.contains("Translate to Indonesian: $englishInput"))
    assertFalse(frameStr.contains("setupComplete"))

    val setupFrame = com.example.data.remote.GeminiTranslatorClient.buildSetupFrame(
      com.example.data.remote.GeminiTranslatorClient.MODEL_LIVE_TRANSLATE
    )
    assertEquals(
      "models/gemini-3.5-live-translate-preview",
      setupFrame.getJSONObject("setup").getString("model")
    )
  }

  @Test
  fun `in-app custom gemini api key persists and updates state`() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = MainViewModel(app)

    viewModel.saveCustomApiKey("  AIzaSyTestCustomKey12345  ")
    assertEquals("AIzaSyTestCustomKey12345", viewModel.uiState.value.customApiKey)
    assertTrue(viewModel.uiState.value.isGeminiApiKeyConfigured)
    assertEquals(
      "AIzaSyTestCustomKey12345",
      com.example.data.remote.GeminiTranslatorClient.activeApiKey
    )

    viewModel.clearCustomApiKey()
    assertEquals("", viewModel.uiState.value.customApiKey)
  }
}

