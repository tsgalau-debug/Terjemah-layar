package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.model.TranslationBlock
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SandboxScreen(
  uiState: MainUiState,
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current
  val scenarios = listOf("Game RPG", "Artikel Tech", "Komik / Webtoon", "Chat Game")

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(8.dp))
      // Header Card
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(20.dp))
          .background(
            Brush.verticalGradient(
              listOf(
                TealPrimary.copy(alpha = 0.35f),
                SlateDarkCard
              )
            )
          )
          .border(1.dp, TealGlow.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
          .padding(18.dp)
      ) {
        Column {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(TealPrimary.copy(alpha = 0.25f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                Icons.Default.TouchApp,
                contentDescription = null,
                tint = TealGlow,
                modifier = Modifier.size(24.dp)
              )
            }
            Column {
              Text(
                text = "Arena Uji Terjemahan",
                color = TextPrimaryDark,
                fontWeight = FontWeight.Bold,
                fontSize = 19.sp
              )
              Text(
                text = "Simulasi langsung in-place overlay & model AI",
                color = TextSecondaryDark,
                fontSize = 12.sp
              )
            }
          }
          Spacer(modifier = Modifier.height(14.dp))
          Text(
            text = "Uji langsung kecerdasan Gemini 3.5 Live Translate dan rendering badge tanpa perlu keluar ke aplikasi lain. Pilih skenario di bawah:",
            color = TextSecondaryDark,
            fontSize = 13.sp,
            lineHeight = 18.sp
          )
        }
      }
    }

    // Scenario Selector Chips
    item {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
          text = "Pilih Skenario Teks:",
          color = TextPrimaryDark,
          fontWeight = FontWeight.SemiBold,
          fontSize = 14.sp
        )
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          scenarios.forEach { scenario ->
            val isSelected = uiState.selectedSandboxScenario == scenario
            FilterChip(
              selected = isSelected,
              onClick = { viewModel.selectSandboxScenario(scenario) },
              label = { Text(scenario, fontSize = 12.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = TealPrimary,
                selectedLabelColor = Color.White,
                containerColor = SlateDarkCard,
                labelColor = TextSecondaryDark
              ),
              border = FilterChipDefaults.filterChipBorder(
                borderColor = if (isSelected) TealGlow else SlateDarkStroke,
                selectedBorderColor = TealGlow,
                enabled = true,
                selected = isSelected
              )
            )
          }
        }
      }
    }

    // Interactive Viewport Simulator
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .border(1.dp, SlateDarkStroke, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = SlateDarkCard)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(if (uiState.sandboxBlocks.isNotEmpty()) SuccessGreen else AmberAccent)
              )
              Text(
                text = "Simulasi Layar: ${uiState.selectedSandboxScenario}",
                color = TextPrimaryDark,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
              )
            }

            Button(
              onClick = { viewModel.triggerSandboxScan() },
              enabled = !uiState.isSandboxScanning,
              colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.testTag("btn_sandbox_scan")
            ) {
              if (uiState.isSandboxScanning) {
                CircularProgressIndicator(
                  modifier = Modifier.size(16.dp),
                  color = Color.White,
                  strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Memindai...", fontSize = 12.sp)
              } else {
                Icon(
                  Icons.Default.AutoAwesome,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp),
                  tint = Color.White
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Pindai Layar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Simulated Phone Frame
          BoxWithConstraints(
            modifier = Modifier
              .fillMaxWidth()
              .height(280.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0xFF0F172A))
              .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
              .padding(12.dp)
          ) {
            val totalWidth = maxWidth
            val totalHeight = maxHeight

            // Default Scenario Mock Background Content
            Column(
              modifier = Modifier.fillMaxSize(),
              verticalArrangement = Arrangement.SpaceBetween
            ) {
              when (uiState.selectedSandboxScenario) {
                "Game RPG" -> {
                  Text(
                    text = "[RPG Quest Log]\nQuest: Defeat the Shadow Dragon in the ancient dungeon.",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                  )
                  Text(
                    text = "[Combat HUD]\nCritical Hit! 2,450 damage dealt to enemy.",
                    color = Color(0xFFE2E8F0),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = "[Bag System]\nInventory is full. Equip your legendary weapon.",
                    color = Color(0xFFCBD5E1),
                    fontSize = 12.sp
                  )
                }
                "Artikel Tech" -> {
                  Text(
                    text = "Headline: On-Device Neural Engine accelerates real-time text processing.",
                    color = Color(0xFFE2E8F0),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                  )
                  Text(
                    text = "Body: Privacy policy: Screen capture permissions remain strictly on-device.",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                  )
                }
                "Komik / Webtoon" -> {
                  Text(
                    text = "Balon Kata 1: \"Wait! Don't go there alone, the monster is dangerous!\"",
                    color = Color(0xFFCBD5E1),
                    fontSize = 13.sp
                  )
                  Text(
                    text = "Balon Kata 2: \"I will protect everyone no matter what happens.\"",
                    color = Color(0xFFE2E8F0),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                  )
                }
                else -> {
                  Text(
                    text = "P1: \"Hey, are you ready for the ranked match tonight?\"",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                  )
                  Text(
                    text = "P2: \"Let's join squad and win the battle together!\"",
                    color = Color(0xFFE2E8F0),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }

            // In-Place Overlay Badges (Simulating Floating Badges on Screen)
            uiState.sandboxBlocks.forEach { block ->
              val isBlockSelected = uiState.selectedSandboxBlock?.id == block.id
              val badgeAlpha = if (isBlockSelected) {
                uiState.overlayOpacity.coerceAtLeast(0.92f)
              } else {
                uiState.overlayOpacity
              }
              Box(
                modifier = Modifier
                  .offset(
                    x = totalWidth * block.xRatio,
                    y = totalHeight * block.yRatio
                  )
                  .width(totalWidth * block.widthRatio)
                  .clip(RoundedCornerShape(8.dp))
                  .background(
                    if (isBlockSelected) Color(0xFF047857).copy(alpha = badgeAlpha)
                    else Color(0xFF0F172A).copy(alpha = badgeAlpha)
                  )
                  .border(
                    width = if (isBlockSelected) 1.5.dp else 1.dp,
                    color = if (isBlockSelected) TealGlow else TealPrimaryLight,
                    shape = RoundedCornerShape(8.dp)
                  )
                  .clickable { viewModel.selectSandboxBlock(block) }
                  .padding(horizontal = 8.dp, vertical = 6.dp)
                  .testTag("inplace_overlay_block_${block.id}")
              ) {
                Column {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = block.translatedText,
                      color = Color.White,
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(TealPrimary.copy(alpha = 0.4f))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                      Text(
                        text = block.category,
                        color = TealGlow,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold
                      )
                    }
                  }
                  Text(
                    text = block.originalText,
                    color = Color(0xFFA7F3D0),
                    fontSize = 9.5.sp,
                    maxLines = 1
                  )
                }
              }
            }

            if (uiState.sandboxBlocks.isEmpty() && !uiState.isSandboxScanning) {
              Box(
                modifier = Modifier
                  .fillMaxSize()
                  .background(Color(0x88000000)),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "Ketuk tombol apung EN→ID atau 'Pindai Layar' untuk menerjemahkan",
                  color = TextSecondaryDark,
                  fontSize = 11.sp
                )
              }
            }

            // Draggable Floating Bubble Trigger inside the Sandbox Viewport
            com.example.ui.components.FloatingBubbleUi(
              isScanning = uiState.isSandboxScanning,
              onSingleTap = {
                if (!uiState.isSandboxScanning) {
                  viewModel.triggerSandboxScan()
                }
              }
            )
          }
        }
      }
    }

    // Selected Block Translation Details Card
    uiState.selectedSandboxBlock?.let { block ->
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, TealGlow.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
          colors = CardDefaults.cardColors(containerColor = SlateDarkCard)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Icon(
                  Icons.Default.AutoAwesome,
                  contentDescription = null,
                  tint = TealGlow,
                  modifier = Modifier.size(18.dp)
                )
                Text(
                  text = "Detail Terjemahan Terpilih",
                  color = TextPrimaryDark,
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp
                )
              }

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(TealPrimary.copy(alpha = 0.25f))
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(
                  text = block.sourceEngine,
                  color = TealGlow,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // English Original
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF0F172A))
                .padding(12.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "ORIGINAL (EN):",
                  color = TextMutedDark,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold
                )
                IconButton(
                  onClick = { viewModel.speakEnglish(block.originalText) },
                  modifier = Modifier.size(28.dp)
                ) {
                  Icon(
                    Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Dengar English",
                    tint = TealPrimaryLight,
                    modifier = Modifier.size(16.dp)
                  )
                }
              }
              Text(
                text = block.originalText,
                color = TextPrimaryDark,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Indonesian Translated
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF064E3B).copy(alpha = 0.4f))
                .border(1.dp, TealPrimary.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                .padding(12.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "TERJEMAHAN (ID):",
                  color = TealGlow,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold
                )
                Row {
                  IconButton(
                    onClick = { viewModel.speakIndonesian(block.translatedText) },
                    modifier = Modifier.size(28.dp)
                  ) {
                    Icon(
                      Icons.AutoMirrored.Filled.VolumeUp,
                      contentDescription = "Dengar Indonesia",
                      tint = TealGlow,
                      modifier = Modifier.size(16.dp)
                    )
                  }
                  IconButton(
                    onClick = {
                      clipboardManager.setText(AnnotatedString(block.translatedText))
                      Toast.makeText(context, "Teks terjemahan disalin!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(28.dp)
                  ) {
                    Icon(
                      Icons.Default.ContentCopy,
                      contentDescription = "Salin Teks",
                      tint = TextSecondaryDark,
                      modifier = Modifier.size(16.dp)
                    )
                  }
                }
              }
              Text(
                text = block.translatedText,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
              )
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
