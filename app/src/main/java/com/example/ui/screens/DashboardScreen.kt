package com.example.ui.screens

import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ScreenShare
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EngineMode
import com.example.ui.MainUiState
import com.example.ui.MainViewModel
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.SlateDarkCard
import com.example.ui.theme.SlateDarkStroke
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TealGlow
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TealPrimaryLight
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark

@Composable
fun DashboardScreen(
  uiState: MainUiState,
  viewModel: MainViewModel,
  historyCount: Int,
  onRequestScreenCapture: () -> Unit,
  onPickScreenshot: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(8.dp))
      // Hero Header
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(24.dp))
          .background(
            Brush.verticalGradient(
              colors = listOf(
                TealPrimary.copy(alpha = 0.35f),
                Color(0xFF0F172A)
              )
            )
          )
          .border(1.dp, TealPrimaryLight.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
          .padding(20.dp)
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(44.dp)
                  .clip(CircleShape)
                  .background(TealPrimary)
                  .border(2.dp, TealGlow, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Translate,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(24.dp)
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "LayarTerjemah",
                  fontSize = 20.sp,
                  fontWeight = FontWeight.Bold,
                  color = TextPrimaryDark
                )
                Text(
                  text = "Perekam & Penerjemah Layar Asli",
                  fontSize = 12.sp,
                  color = TealGlow
                )
              }
            }

            // Status Badge
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(
                  if (uiState.isServiceRunning) SuccessGreen.copy(alpha = 0.2f)
                  else Color(0xFF334155)
                )
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Text(
                text = if (uiState.isServiceRunning) "● AKTIF" else "○ NONAKTIF",
                color = if (uiState.isServiceRunning) SuccessGreen else TextSecondaryDark,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0xFF0F172A).copy(alpha = 0.7f))
              .border(1.dp, TealGlow.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
              .padding(horizontal = 10.dp, vertical = 5.dp)
          ) {
            Box(
              modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(if (uiState.isGeminiApiKeyConfigured) SuccessGreen else AmberAccent)
            )
            Spacer(modifier = Modifier.width(7.dp))
            Text(
              text = "Model Aktif: Gemini 3.5 Live Translate",
              color = if (uiState.isGeminiApiKeyConfigured) TealGlow else TextSecondaryDark,
              fontSize = 11.5.sp,
              fontWeight = FontWeight.SemiBold
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "Merekam tampilan layar nyata Anda dan menerjemahkan teks bahasa Inggris secara instan menggunakan Google ML Kit OCR dan Gemini 3.5 Live Translate.",
            fontSize = 13.sp,
            color = TextSecondaryDark,
            lineHeight = 18.sp
          )
        }
      }
    }

    // Real Screen Capture (MediaProjection) Permission Card
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("screen_capture_permission_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
        border = androidx.compose.foundation.BorderStroke(
          width = 1.dp,
          color = if (uiState.isScreenCaptureReady) SuccessGreen.copy(alpha = 0.5f) else AmberAccent.copy(alpha = 0.6f)
        )
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(
                    if (uiState.isScreenCaptureReady) SuccessGreen.copy(alpha = 0.2f)
                    else AmberAccent.copy(alpha = 0.2f)
                  ),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.ScreenShare,
                  contentDescription = null,
                  tint = if (uiState.isScreenCaptureReady) SuccessGreen else AmberAccent
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "Izin Perekaman Layar (MediaProjection)",
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Bold,
                  color = TextPrimaryDark
                )
                Text(
                  text = if (uiState.isScreenCaptureReady)
                    "Siap merekam layar game/aplikasi nyata"
                  else "Diperlukan untuk membaca teks di layar asli",
                  fontSize = 12.sp,
                  color = if (uiState.isScreenCaptureReady) SuccessGreen else AmberAccent
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          Button(
            onClick = onRequestScreenCapture,
            colors = ButtonDefaults.buttonColors(
              containerColor = if (uiState.isScreenCaptureReady) Color(0xFF1E293B) else AmberAccent
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth().testTag("grant_screen_capture_button")
          ) {
            Icon(
              imageVector = if (uiState.isScreenCaptureReady) Icons.Default.CheckCircle else Icons.AutoMirrored.Filled.ScreenShare,
              contentDescription = null,
              tint = if (uiState.isScreenCaptureReady) SuccessGreen else Color(0xFF0F172A),
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (uiState.isScreenCaptureReady) "Izin Rekam Layar Aktif (Ketuk Ulang jika Diperlukan)" else "Izinkan Perekaman Layar Sekarang",
              color = if (uiState.isScreenCaptureReady) TextSecondaryDark else Color(0xFF0F172A),
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }

    // Master Floating Service Toggle Card
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("floating_service_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateDarkStroke)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(if (uiState.isServiceRunning) AmberAccent else TealPrimary.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Widgets,
                  contentDescription = null,
                  tint = if (uiState.isServiceRunning) Color(0xFF0F172A) else TealGlow
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "Tombol Apung Layar",
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Bold,
                  color = TextPrimaryDark
                )
                Text(
                  text = if (uiState.isServiceRunning) "Tombol melayang di layar luar" else "Aktifkan untuk melayang di atas game",
                  fontSize = 12.sp,
                  color = TextSecondaryDark
                )
              }
            }

            Switch(
              checked = uiState.isServiceRunning,
              onCheckedChange = { viewModel.toggleFloatingService() },
              colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = TealPrimary,
                uncheckedTrackColor = Color(0xFF334155)
              ),
              modifier = Modifier.testTag("floating_service_switch")
            )
          }

          if (!uiState.hasOverlayPermission) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(AmberAccent.copy(alpha = 0.12f))
                .border(1.dp, AmberAccent.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                .clickable {
                  val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                  }
                  context.startActivity(intent)
                }
                .padding(10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = AmberAccent,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Butuh Izin 'Tampilkan di Atas Aplikasi Lain'. Ketuk untuk membuka pengaturan.",
                fontSize = 11.sp,
                color = AmberAccent,
                fontWeight = FontWeight.Medium
              )
            }
          }
        }
      }
    }

    // Direct Gallery Screenshot Scanner Option
    item {
      Card(
        modifier = Modifier.fillMaxWidth().testTag("gallery_screenshot_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateDarkStroke)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(TealPrimary.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.AddPhotoAlternate,
                  contentDescription = null,
                  tint = TealGlow
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "Pindai Screenshot dari Galeri",
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Bold,
                  color = TextPrimaryDark
                )
                Text(
                  text = "Pilih foto screenshot game/komik untuk diterjemahkan",
                  fontSize = 12.sp,
                  color = TextSecondaryDark
                )
              }
            }

            OutlinedButton(
              onClick = onPickScreenshot,
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.testTag("pick_screenshot_button")
            ) {
              Text(if (uiState.isProcessingImage) "Memproses..." else "Pilih Foto", fontSize = 12.sp)
            }
          }

          // Show result if available
          if (uiState.lastCapturedTranslated != null) {
            Spacer(modifier = Modifier.height(14.dp))
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF0F172A))
                .border(1.dp, TealGlow.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                .padding(12.dp)
            ) {
              Column {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "HASIL TERJEMAHAN GAMBAR:",
                    color = TealGlow,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                  )
                  Row {
                    IconButton(
                      onClick = { viewModel.speakIndonesian(uiState.lastCapturedTranslated) },
                      modifier = Modifier.size(28.dp)
                    ) {
                      Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Suara",
                        tint = TealGlow,
                        modifier = Modifier.size(16.dp)
                      )
                    }
                    IconButton(
                      onClick = {
                        clipboardManager.setText(AnnotatedString(uiState.lastCapturedTranslated))
                        Toast.makeText(context, "Terjemahan disalin!", Toast.LENGTH_SHORT).show()
                      },
                      modifier = Modifier.size(28.dp)
                    ) {
                      Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Salin",
                        tint = TextSecondaryDark,
                        modifier = Modifier.size(16.dp)
                      )
                    }
                  }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = uiState.lastCapturedTranslated,
                  color = Color.White,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.SemiBold
                )
                if (uiState.lastCapturedOriginal != null) {
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = "Asli: ${uiState.lastCapturedOriginal}",
                    color = TextMutedDark,
                    fontSize = 11.sp,
                    maxLines = 2
                  )
                }
              }
            }
          }
        }
      }
    }

    // Real-World Usage Workflow
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateDarkStroke)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "Cara Menggunakan di Layar Asli",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark
          )
          Spacer(modifier = Modifier.height(12.dp))

          WorkflowStep(
            number = "1",
            title = "Berikan Izin Rekam Layar",
            desc = "Ketuk tombol 'Izinkan Perekaman Layar' di atas dan pilih 'Mulai sekarang'."
          )
          WorkflowStep(
            number = "2",
            title = "Nyalakan Tombol Apung",
            desc = "Aktifkan sakelar tombol apung agar logo EN→ID melayang di layar."
          )
          WorkflowStep(
            number = "3",
            title = "Buka Game atau Aplikasi Bahasa Inggris",
            desc = "Buka aplikasi apa saja (Game RPG, Webtoon, Twitter, Chrome, dokumen PDF)."
          )
          WorkflowStep(
            number = "4",
            title = "Ketuk Tombol Apung EN→ID",
            desc = "Tombol apung akan langsung memotret layar asli, membaca teks via Google ML Kit OCR, dan menampilkan terjemahan Indonesia langsung di layar tanpa keluar dari game."
          )
        }
      }
    }

    // Engine Selection Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateDarkStroke)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = TealGlow,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Mesin Terjemahan",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimaryDark
            )
          }
          Spacer(modifier = Modifier.height(12.dp))

          EngineMode.entries.forEach { mode ->
            val isSelected = uiState.engineMode == mode
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                  if (isSelected) TealPrimary.copy(alpha = 0.2f)
                  else Color(0xFF0F172A).copy(alpha = 0.5f)
                )
                .border(
                  width = if (isSelected) 1.5.dp else 1.dp,
                  color = if (isSelected) TealPrimaryLight else SlateDarkStroke,
                  shape = RoundedCornerShape(12.dp)
                )
                .clickable { viewModel.setEngineMode(mode) }
                .padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(18.dp)
                  .clip(CircleShape)
                  .border(2.dp, if (isSelected) TealGlow else TextMutedDark, CircleShape)
                  .background(if (isSelected) TealGlow else Color.Transparent)
              )
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = mode.title,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = if (isSelected) Color.White else TextSecondaryDark
                )
                Text(
                  text = mode.subtitle,
                  fontSize = 11.sp,
                  color = TextMutedDark
                )
              }
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
fun WorkflowStep(number: String, title: String, desc: String) {
  Row(
    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
    verticalAlignment = Alignment.Top
  ) {
    Box(
      modifier = Modifier
        .size(24.dp)
        .clip(CircleShape)
        .background(TealPrimary),
      contentAlignment = Alignment.Center
    ) {
      Text(number, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
    Spacer(modifier = Modifier.width(10.dp))
    Column {
      Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
      Text(desc, fontSize = 11.sp, color = TextSecondaryDark, lineHeight = 15.sp)
    }
  }
}
