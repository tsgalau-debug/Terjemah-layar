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
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainUiState
import com.example.ui.MainViewModel
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.SlateDarkCard
import com.example.ui.theme.SlateDarkStroke
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TealGlow
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import kotlin.math.roundToInt

@Composable
fun SettingsScreen(
  uiState: MainUiState,
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current
  var apiKeyInput by remember(uiState.customApiKey) { mutableStateOf(uiState.customApiKey) }
  var showApiKeyText by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(4.dp))
      Column {
        Text(
          text = "Pengaturan LayarTerjemah",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimaryDark
        )
        Text(
          text = "Konfigurasi tampilan overlay, suara DeepMind Gemini, dan izin sistem",
          fontSize = 12.sp,
          color = TextSecondaryDark
        )
      }
    }

    // Overlay Opacity Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth().testTag("settings_opacity_card"),
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
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Layers,
                contentDescription = null,
                tint = TealGlow,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Transparansi Lapisan Teks",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
              )
            }

            Text(
              text = "${(uiState.overlayOpacity * 100).roundToInt()}%",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = TealGlow
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Slider(
            value = uiState.overlayOpacity,
            onValueChange = { viewModel.setOverlayOpacity(it) },
            valueRange = 0.5f..1.0f,
            steps = 9,
            colors = SliderDefaults.colors(
              thumbColor = TealGlow,
              activeTrackColor = TealPrimary,
              inactiveTrackColor = Color(0xFF334155)
            ),
            modifier = Modifier.testTag("overlay_opacity_slider")
          )

          Text(
            text = "Sesuaikan kepekatan latar belakang kotak teks terjemahan agar tulisan mudah dibaca di atas game atau aplikasi.",
            fontSize = 11.sp,
            color = TextMutedDark,
            lineHeight = 15.sp
          )
        }
      }
    }

    // Google DeepMind Gemini Audio Voice Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth().testTag("gemini_audio_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
        border = androidx.compose.foundation.BorderStroke(
          1.dp,
          if (uiState.useGeminiAiAudio) TealPrimary else SlateDarkStroke
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
                  .size(38.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .background(TealPrimary.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.GraphicEq,
                  contentDescription = null,
                  tint = TealGlow,
                  modifier = Modifier.size(20.dp)
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "DeepMind Gemini AI Audio Voice",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  color = TextPrimaryDark
                )
                Text(
                  text = "Suara alami berbasis generative voice design",
                  fontSize = 11.sp,
                  color = TealGlow
                )
              }
            }

            Switch(
              checked = uiState.useGeminiAiAudio,
              onCheckedChange = { viewModel.setUseGeminiAudio(it) },
              colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = TealPrimary,
                uncheckedTrackColor = Color(0xFF334155)
              )
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "Menggunakan model audio DeepMind (gemini-2.5-flash-preview-tts) untuk menghasilkan intonasi suara manusia yang luwes saat membacakan teks terjemahan.",
            fontSize = 11.sp,
            color = TextSecondaryDark,
            lineHeight = 15.sp
          )

          if (uiState.useGeminiAiAudio) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "PILIHAN KARAKTER SUARA AI:",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = AmberAccent
            )
            Spacer(modifier = Modifier.height(6.dp))

            val voices = listOf(
              "Kore" to "Kore (Lembut)",
              "Puck" to "Puck (Ceria)",
              "Fenrir" to "Fenrir (Tegas)",
              "Aoede" to "Aoede (Melodis)"
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              voices.forEach { (voiceId, label) ->
                val isSelected = uiState.selectedGeminiVoice == voiceId
                FilterChip(
                  selected = isSelected,
                  onClick = { viewModel.setGeminiVoice(voiceId) },
                  label = { Text(label, fontSize = 10.sp) },
                  colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = TealPrimary,
                    selectedLabelColor = Color.White,
                    containerColor = Color(0xFF0F172A),
                    labelColor = TextSecondaryDark
                  ),
                  border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = if (isSelected) TealGlow else SlateDarkStroke
                  )
                )
              }
            }
          }
        }
      }
    }

    // Gemini AI API Key Input & Status Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth().testTag("settings_api_card"),
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
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.VpnKey,
                contentDescription = null,
                tint = AmberAccent,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Kunci API Gemini AI",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
              )
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(
                  if (uiState.isGeminiApiKeyConfigured) SuccessGreen.copy(alpha = 0.2f)
                  else AmberAccent.copy(alpha = 0.15f)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = when {
                  uiState.customApiKey.isNotBlank() -> "KEY APLIKASI AKTIF"
                  uiState.isGeminiApiKeyConfigured -> "TERHUBUNG"
                  else -> "KAMUS OFFLINE AKTIF"
                },
                color = if (uiState.isGeminiApiKeyConfigured) SuccessGreen else AmberAccent,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "Masukkan atau tempel (paste) Gemini API Key Anda langsung di bawah ini. Kunci disimpan secara lokal di perangkat Anda dan otomatis dipakai oleh tombol apung maupun Arena Uji.",
            fontSize = 12.sp,
            color = TextSecondaryDark,
            lineHeight = 16.sp
          )

          Spacer(modifier = Modifier.height(12.dp))

          OutlinedTextField(
            value = apiKeyInput,
            onValueChange = { apiKeyInput = it },
            label = { Text("GEMINI_API_KEY (AIzaSy...)", fontSize = 12.sp) },
            placeholder = { Text("Tempel API Key Gemini Anda di sini", fontSize = 12.sp, color = TextMutedDark) },
            singleLine = true,
            visualTransformation = if (showApiKeyText) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
              Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                  onClick = { showApiKeyText = !showApiKeyText },
                  modifier = Modifier.testTag("toggle_api_key_visibility_button")
                ) {
                  Icon(
                    imageVector = if (showApiKeyText) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (showApiKeyText) "Sembunyikan Key" else "Tampilkan Key",
                    tint = TextSecondaryDark
                  )
                }
                IconButton(
                  onClick = {
                    val clipText = clipboardManager.getText()?.text?.trim() ?: ""
                    if (clipText.isNotEmpty()) {
                      apiKeyInput = clipText
                      Toast.makeText(context, "API Key ditempel dari clipboard", Toast.LENGTH_SHORT).show()
                    } else {
                      Toast.makeText(context, "Clipboard kosong", Toast.LENGTH_SHORT).show()
                    }
                  },
                  modifier = Modifier.testTag("paste_api_key_button")
                ) {
                  Icon(
                    imageVector = Icons.Default.ContentPaste,
                    contentDescription = "Tempel API Key",
                    tint = TealGlow
                  )
                }
              }
            },
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White,
              focusedBorderColor = TealGlow,
              unfocusedBorderColor = SlateDarkStroke,
              focusedLabelColor = TealGlow,
              unfocusedLabelColor = TextSecondaryDark,
              cursorColor = TealGlow
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("gemini_api_key_input")
          )

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = {
                viewModel.saveCustomApiKey(apiKeyInput)
                Toast.makeText(
                  context,
                  if (apiKeyInput.isNotBlank()) "API Key Gemini berhasil disimpan & diaktifkan!"
                  else "API Key dikosongkan",
                  Toast.LENGTH_SHORT
                ).show()
              },
              colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .weight(1f)
                .testTag("save_api_key_button")
            ) {
              Icon(
                imageVector = Icons.Default.Save,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color.White
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text("Simpan API Key", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            if (uiState.customApiKey.isNotBlank() || apiKeyInput.isNotBlank()) {
              OutlinedButton(
                onClick = {
                  apiKeyInput = ""
                  viewModel.clearCustomApiKey()
                  Toast.makeText(context, "API Key kustom dihapus", Toast.LENGTH_SHORT).show()
                },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("clear_api_key_button")
              ) {
                Icon(
                  imageVector = Icons.Default.DeleteOutline,
                  contentDescription = "Hapus API Key",
                  modifier = Modifier.size(16.dp),
                  tint = AmberAccent
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Reset", fontSize = 12.sp, color = AmberAccent)
              }
            }
          }
        }
      }
    }

    // Text to Speech Audio Test
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
              imageVector = Icons.AutoMirrored.Filled.VolumeUp,
              contentDescription = null,
              tint = TealGlow,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Uji Suara Pengucapan",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimaryDark
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = if (uiState.useGeminiAiAudio)
              "Mendengarkan sampel suara DeepMind Gemini AI (${uiState.selectedGeminiVoice}):"
            else
              "Mendengarkan pelafalan Android TextToSpeech standar:",
            fontSize = 12.sp,
            color = TextSecondaryDark
          )

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedButton(
              onClick = { viewModel.speakEnglish("Main Quest: Defeat the Boss and equip your legendary weapon.") },
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.weight(1f).testTag("test_tts_en_button")
            ) {
              Text("Suara Inggris", fontSize = 12.sp)
            }

            OutlinedButton(
              onClick = { viewModel.speakIndonesian("Misi Utama: Kalahkan Bos dan pasang senjata legendaris milikmu.") },
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.weight(1f).testTag("test_tts_id_button")
            ) {
              Text("Suara Indonesia", fontSize = 12.sp)
            }
          }
        }
      }
    }

    // System Permission Card
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
              imageVector = Icons.Default.Security,
              contentDescription = null,
              tint = if (uiState.hasOverlayPermission) SuccessGreen else AmberAccent,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Izin Tampilkan di Atas Aplikasi",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimaryDark
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = if (uiState.hasOverlayPermission)
              "Izin telah diberikan. Tombol apung dapat ditampilkan di atas game dan aplikasi apa pun."
            else
              "Izin belum aktif. Diperlukan agar tombol apung bisa melayang di atas aplikasi lain.",
            fontSize = 12.sp,
            color = TextSecondaryDark
          )

          Spacer(modifier = Modifier.height(12.dp))

          Button(
            onClick = {
              val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
              }
              context.startActivity(intent)
            },
            colors = ButtonDefaults.buttonColors(
              containerColor = if (uiState.hasOverlayPermission) SlateDarkStroke else TealPrimary
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth().testTag("open_permission_settings_button")
          ) {
            Text(
              text = if (uiState.hasOverlayPermission) "Buka Pengaturan Izin Android" else "Aktifkan Izin Overlay",
              fontSize = 12.sp
            )
          }
        }
      }
    }

    // App Info Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateDarkStroke)
      ) {
        Row(
          modifier = Modifier.padding(18.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint = TextMutedDark,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = "LayarTerjemah v1.1",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimaryDark
            )
            Text(
              text = "MediaProjection + ML Kit OCR + DeepMind Gemini Audio",
              fontSize = 11.sp,
              color = TextMutedDark
            )
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(30.dp))
    }
  }
}
