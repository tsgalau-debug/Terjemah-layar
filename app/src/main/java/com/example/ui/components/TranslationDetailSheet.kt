package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TranslationBlock
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.SlateDarkCard
import com.example.ui.theme.SlateDarkStroke
import com.example.ui.theme.TealGlow
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark

@Composable
fun TranslationDetailSheet(
  block: TranslationBlock,
  isStarred: Boolean,
  onDismiss: () -> Unit,
  onCopyOriginal: (String) -> Unit,
  onCopyTranslation: (String) -> Unit,
  onSpeakEnglish: (String) -> Unit,
  onSpeakIndonesian: (String) -> Unit,
  onToggleStar: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp, vertical = 8.dp)
      .testTag("translation_detail_sheet"),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateDarkStroke)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      // Header with engine badge and close button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(TealPrimary.copy(alpha = 0.25f))
              .border(1.dp, TealPrimary, RoundedCornerShape(6.dp))
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Text(
              text = block.sourceEngine,
              color = TealGlow,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(AmberAccent.copy(alpha = 0.2f))
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Text(
              text = block.category,
              color = AmberAccent,
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }

        Row {
          IconButton(
            onClick = onToggleStar,
            modifier = Modifier.size(36.dp).testTag("detail_sheet_star_button")
          ) {
            Icon(
              imageVector = if (isStarred) Icons.Default.Star else Icons.Default.StarBorder,
              contentDescription = "Simpan ke Favorit",
              tint = if (isStarred) AmberAccent else TextMutedDark,
              modifier = Modifier.size(20.dp)
            )
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(36.dp).testTag("detail_sheet_close_button")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Tutup Kartu",
              tint = TextSecondaryDark,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Section Original English
      Text(
        text = "TEKS ASLI (ENGLISH)",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = TextSecondaryDark,
        letterSpacing = 1.sp
      )
      Spacer(modifier = Modifier.height(4.dp))
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(Color(0xFF0F172A).copy(alpha = 0.6f))
          .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = block.originalText,
          fontSize = 15.sp,
          color = TextPrimaryDark,
          modifier = Modifier.weight(1f),
          fontWeight = FontWeight.Normal
        )
        Row {
          IconButton(
            onClick = { onSpeakEnglish(block.originalText) },
            modifier = Modifier.size(32.dp).testTag("speak_english_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.VolumeUp,
              contentDescription = "Dengarkan Bahasa Inggris",
              tint = TealGlow,
              modifier = Modifier.size(18.dp)
            )
          }
          IconButton(
            onClick = { onCopyOriginal(block.originalText) },
            modifier = Modifier.size(32.dp).testTag("copy_english_button")
          ) {
            Icon(
              imageVector = Icons.Default.ContentCopy,
              contentDescription = "Salin Teks Inggris",
              tint = TextSecondaryDark,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Section Translated Indonesian
      Text(
        text = "HASIL TERJEMAHAN (BAHASA INDONESIA)",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = TealGlow,
        letterSpacing = 1.sp
      )
      Spacer(modifier = Modifier.height(4.dp))
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(TealPrimary.copy(alpha = 0.12f))
          .border(1.dp, TealPrimary.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
          .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = block.translatedText,
          fontSize = 16.sp,
          color = Color.White,
          modifier = Modifier.weight(1f),
          fontWeight = FontWeight.SemiBold
        )
        Row {
          IconButton(
            onClick = { onSpeakIndonesian(block.translatedText) },
            modifier = Modifier.size(32.dp).testTag("speak_indonesian_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.VolumeUp,
              contentDescription = "Dengarkan Bahasa Indonesia",
              tint = TealGlow,
              modifier = Modifier.size(18.dp)
            )
          }
          IconButton(
            onClick = { onCopyTranslation(block.translatedText) },
            modifier = Modifier.size(32.dp).testTag("copy_indonesian_button")
          ) {
            Icon(
              imageVector = Icons.Default.ContentCopy,
              contentDescription = "Salin Terjemahan",
              tint = TealGlow,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Quick Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
      ) {
        FilledTonalButton(
          onClick = { onCopyTranslation(block.translatedText) },
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.testTag("detail_sheet_copy_all")
        ) {
          Icon(
            imageVector = Icons.Default.ContentCopy,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "Salin Terjemahan", fontSize = 12.sp)
        }
      }
    }
  }
}
