package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.TranslationEntity
import com.example.ui.MainViewModel
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.SlateDarkCard
import com.example.ui.theme.SlateDarkStroke
import com.example.ui.theme.TealGlow
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
  historyList: List<TranslationEntity>,
  starredList: List<TranslationEntity>,
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current
  var showFavoritesOnly by remember { mutableStateOf(false) }
  var showClearConfirmDialog by remember { mutableStateOf(false) }

  val displayedList = if (showFavoritesOnly) starredList else historyList
  val dateFormat = remember { SimpleDateFormat("dd MMM, HH:mm", Locale.forLanguageTag("id-ID")) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
  ) {
    // Header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "Riwayat Terjemahan Layar",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimaryDark
        )
        Text(
          text = "${displayedList.size} sesi tersimpan",
          fontSize = 12.sp,
          color = TextSecondaryDark
        )
      }

      if (historyList.isNotEmpty()) {
        IconButton(
          onClick = { showClearConfirmDialog = true },
          modifier = Modifier.testTag("clear_history_icon_button")
        ) {
          Icon(
            imageVector = Icons.Default.DeleteSweep,
            contentDescription = "Hapus Riwayat",
            tint = TextMutedDark
          )
        }
      }
    }

    // Filter Chips
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 12.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      FilterChip(
        selected = !showFavoritesOnly,
        onClick = { showFavoritesOnly = false },
        label = { Text("Semua (${historyList.size})") },
        colors = FilterChipDefaults.filterChipColors(
          selectedContainerColor = TealPrimary,
          selectedLabelColor = Color.White,
          containerColor = SlateDarkCard,
          labelColor = TextSecondaryDark
        ),
        border = FilterChipDefaults.filterChipBorder(
          enabled = true,
          selected = !showFavoritesOnly,
          borderColor = if (!showFavoritesOnly) TealGlow else SlateDarkStroke
        )
      )

      FilterChip(
        selected = showFavoritesOnly,
        onClick = { showFavoritesOnly = true },
        label = { Text("Favorit (${starredList.size})") },
        colors = FilterChipDefaults.filterChipColors(
          selectedContainerColor = AmberAccent,
          selectedLabelColor = Color(0xFF0F172A),
          containerColor = SlateDarkCard,
          labelColor = TextSecondaryDark
        ),
        border = FilterChipDefaults.filterChipBorder(
          enabled = true,
          selected = showFavoritesOnly,
          borderColor = if (showFavoritesOnly) AmberAccent else SlateDarkStroke
        )
      )
    }

    // List or Empty State
    if (displayedList.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(bottom = 40.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(
            imageVector = Icons.Default.History,
            contentDescription = null,
            tint = TextMutedDark,
            modifier = Modifier.size(56.dp)
          )
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = if (showFavoritesOnly) "Belum ada terjemahan yang difavoritkan"
            else "Belum ada riwayat pemindaian layar",
            color = TextSecondaryDark,
            fontSize = 14.sp
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Buka Arena Uji untuk memindai dan menerjemahkan teks",
            color = TextMutedDark,
            fontSize = 12.sp
          )
        }
      }
    } else {
      LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        items(displayedList, key = { it.id }) { item ->
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .testTag("history_item_${item.id}"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, SlateDarkStroke)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(6.dp))
                      .background(TealPrimary.copy(alpha = 0.2f))
                      .padding(horizontal = 6.dp, vertical = 2.dp)
                  ) {
                    Text(
                      text = item.engineUsed,
                      color = TealGlow,
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = item.sourceContext,
                    color = TextSecondaryDark,
                    fontSize = 11.sp
                  )
                }

                Text(
                  text = dateFormat.format(Date(item.timestamp)),
                  color = TextMutedDark,
                  fontSize = 11.sp
                )
              }

              Spacer(modifier = Modifier.height(8.dp))

              // Original
              Text(
                text = item.originalText,
                color = TextSecondaryDark,
                fontSize = 12.sp,
                maxLines = 2
              )

              Spacer(modifier = Modifier.height(4.dp))

              // Translated
              Text(
                text = item.translatedText,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 3
              )

              Spacer(modifier = Modifier.height(8.dp))

              // Actions
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
              ) {
                IconButton(
                  onClick = { viewModel.toggleStarredHistory(item) },
                  modifier = Modifier.size(32.dp)
                ) {
                  Icon(
                    imageVector = if (item.isStarred) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = "Favorit",
                    tint = if (item.isStarred) AmberAccent else TextMutedDark,
                    modifier = Modifier.size(18.dp)
                  )
                }

                IconButton(
                  onClick = {
                    clipboardManager.setText(AnnotatedString(item.translatedText))
                    Toast.makeText(context, "Terjemahan disalin!", Toast.LENGTH_SHORT).show()
                  },
                  modifier = Modifier.size(32.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Salin",
                    tint = TealGlow,
                    modifier = Modifier.size(18.dp)
                  )
                }

                IconButton(
                  onClick = { viewModel.deleteHistoryItem(item) },
                  modifier = Modifier.size(32.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Hapus",
                    tint = TextMutedDark,
                    modifier = Modifier.size(18.dp)
                  )
                }
              }
            }
          }
        }

        item {
          Spacer(modifier = Modifier.height(20.dp))
        }
      }
    }
  }

  if (showClearConfirmDialog) {
    AlertDialog(
      onDismissRequest = { showClearConfirmDialog = false },
      title = { Text("Hapus Semua Riwayat?", color = TextPrimaryDark) },
      text = { Text("Seluruh riwayat pemindaian layar akan dihapus permanen.", color = TextSecondaryDark) },
      confirmButton = {
        TextButton(
          onClick = {
            viewModel.clearAllHistory()
            showClearConfirmDialog = false
          }
        ) {
          Text("Hapus", color = Color(0xFFEF4444))
        }
      },
      dismissButton = {
        TextButton(onClick = { showClearConfirmDialog = false }) {
          Text("Batal", color = TextSecondaryDark)
        }
      },
      containerColor = SlateDarkCard
    )
  }
}
