package com.example

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.FloatingOverlayService
import com.example.ui.MainViewModel
import com.example.ui.ScreenTab
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.SandboxScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.VocabularyScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SlateDarkBackground
import com.example.ui.theme.SlateDarkCard
import com.example.ui.theme.TealGlow
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TextMutedDark

class MainActivity : ComponentActivity() {

  private val viewModel: MainViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      MyApplicationTheme(darkTheme = true) {
        val uiState by viewModel.uiState.collectAsState()
        val historyList by viewModel.historyList.collectAsState()
        val starredList by viewModel.starredList.collectAsState()

        val screenCaptureLauncher = rememberLauncherForActivityResult(
          contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
          if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            viewModel.onScreenCapturePermissionGranted(result.resultCode, result.data)
          } else {
            Toast.makeText(this, "Izin rekam layar ditolak.", Toast.LENGTH_SHORT).show()
          }
        }

        val photoPickerLauncher = rememberLauncherForActivityResult(
          contract = ActivityResultContracts.PickVisualMedia()
        ) { uri ->
          if (uri != null) {
            try {
              val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(contentResolver, uri)
                ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                  decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                  decoder.isMutableRequired = true
                }
              } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(contentResolver, uri)
              }
              viewModel.processScreenshotBitmap(bitmap)
            } catch (e: Exception) {
              Toast.makeText(this, "Gagal memproses screenshot: ${e.message}", Toast.LENGTH_SHORT).show()
            }
          }
        }

        fun requestScreenCapture() {
          val mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
          val captureIntent = mediaProjectionManager.createScreenCaptureIntent()
          screenCaptureLauncher.launch(captureIntent)
        }

        // Check incoming intent to request capture
        LaunchedEffect(intent) {
          if (intent?.getBooleanExtra("REQUEST_SCREEN_CAPTURE_PERMISSION", false) == true) {
            requestScreenCapture()
          }
        }

        // Handle Back navigation to Dashboard
        if (uiState.currentTab != ScreenTab.DASHBOARD) {
          BackHandler {
            viewModel.setTab(ScreenTab.DASHBOARD)
          }
        }

        Scaffold(
          modifier = Modifier.fillMaxSize(),
          containerColor = SlateDarkBackground,
          bottomBar = {
            AppBottomNavigation(
              currentTab = uiState.currentTab,
              onTabSelected = { viewModel.setTab(it) }
            )
          }
        ) { innerPadding ->
          Box(
            modifier = Modifier
              .fillMaxSize()
              .padding(innerPadding)
              .background(SlateDarkBackground)
          ) {
            when (uiState.currentTab) {
              ScreenTab.DASHBOARD -> DashboardScreen(
                uiState = uiState,
                viewModel = viewModel,
                historyCount = historyList.size,
                onRequestScreenCapture = { requestScreenCapture() },
                onPickScreenshot = {
                  photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
              )
              ScreenTab.SANDBOX -> SandboxScreen(
                uiState = uiState,
                viewModel = viewModel
              )
              ScreenTab.HISTORY -> HistoryScreen(
                historyList = historyList,
                starredList = starredList,
                viewModel = viewModel
              )
              ScreenTab.VOCABULARY -> VocabularyScreen(
                vocabularyList = uiState.vocabularyList,
                viewModel = viewModel
              )
              ScreenTab.SETTINGS -> SettingsScreen(
                uiState = uiState,
                viewModel = viewModel
              )
            }
          }
        }
      }
    }
  }

  override fun onResume() {
    super.onResume()
    viewModel.checkPermissions()
  }
}

@Composable
fun AppBottomNavigation(
  currentTab: ScreenTab,
  onTabSelected: (ScreenTab) -> Unit
) {
  NavigationBar(
    containerColor = SlateDarkCard,
    tonalElevation = 8.dp,
    modifier = Modifier.testTag("app_bottom_navigation")
  ) {
    NavigationBarItem(
      selected = currentTab == ScreenTab.DASHBOARD,
      onClick = { onTabSelected(ScreenTab.DASHBOARD) },
      icon = { Icon(Icons.Default.Dashboard, contentDescription = "Beranda", modifier = Modifier.size(20.dp)) },
      label = { Text("Beranda", fontSize = 10.sp) },
      colors = navigationItemColors(),
      modifier = Modifier.testTag("nav_tab_dashboard")
    )

    NavigationBarItem(
      selected = currentTab == ScreenTab.SANDBOX,
      onClick = { onTabSelected(ScreenTab.SANDBOX) },
      icon = { Icon(Icons.Default.TouchApp, contentDescription = "Arena Uji", modifier = Modifier.size(20.dp)) },
      label = { Text("Arena Uji", fontSize = 10.sp) },
      colors = navigationItemColors(),
      modifier = Modifier.testTag("nav_tab_sandbox")
    )

    NavigationBarItem(
      selected = currentTab == ScreenTab.HISTORY,
      onClick = { onTabSelected(ScreenTab.HISTORY) },
      icon = { Icon(Icons.Default.History, contentDescription = "Riwayat", modifier = Modifier.size(20.dp)) },
      label = { Text("Riwayat", fontSize = 10.sp) },
      colors = navigationItemColors(),
      modifier = Modifier.testTag("nav_tab_history")
    )

    NavigationBarItem(
      selected = currentTab == ScreenTab.VOCABULARY,
      onClick = { onTabSelected(ScreenTab.VOCABULARY) },
      icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = "Kamus", modifier = Modifier.size(20.dp)) },
      label = { Text("Kamus", fontSize = 10.sp) },
      colors = navigationItemColors(),
      modifier = Modifier.testTag("nav_tab_vocabulary")
    )

    NavigationBarItem(
      selected = currentTab == ScreenTab.SETTINGS,
      onClick = { onTabSelected(ScreenTab.SETTINGS) },
      icon = { Icon(Icons.Default.Settings, contentDescription = "Pengaturan", modifier = Modifier.size(20.dp)) },
      label = { Text("Pengaturan", fontSize = 10.sp) },
      colors = navigationItemColors(),
      modifier = Modifier.testTag("nav_tab_settings")
    )
  }
}

@Composable
private fun navigationItemColors() = NavigationBarItemDefaults.colors(
  selectedIconColor = TealGlow,
  selectedTextColor = TealGlow,
  indicatorColor = TealPrimary.copy(alpha = 0.35f),
  unselectedIconColor = TextMutedDark,
  unselectedTextColor = TextMutedDark
)
