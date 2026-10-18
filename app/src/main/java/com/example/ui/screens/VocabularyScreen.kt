package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VocabularyItem
import com.example.ui.MainViewModel
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.SlateDarkCard
import com.example.ui.theme.SlateDarkStroke
import com.example.ui.theme.TealGlow
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark

@Composable
fun VocabularyScreen(
  vocabularyList: List<VocabularyItem>,
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current
  var selectedCategory by remember { mutableStateOf("Semua") }

  val categories = listOf("Semua", "Game", "Sistem", "Teknologi")
  val filteredList = if (selectedCategory == "Semua") vocabularyList
  else vocabularyList.filter { it.category == selectedCategory }

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
  ) {
    // Header
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
      Text(
        text = "Kamus Istilah Game & Layar",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimaryDark
      )
      Text(
        text = "Istilah bahasa Inggris yang sering muncul di game dan aplikasi",
        fontSize = 12.sp,
        color = TextSecondaryDark
      )
    }

    // Category Filter Chips
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 12.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      categories.forEach { category ->
        val isSelected = category == selectedCategory
        FilterChip(
          selected = isSelected,
          onClick = { selectedCategory = category },
          label = { Text(category, fontSize = 12.sp) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = TealPrimary,
            selectedLabelColor = Color.White,
            containerColor = SlateDarkCard,
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

    // List of Vocabulary Items
    LazyColumn(
      verticalArrangement = Arrangement.spacedBy(10.dp),
      modifier = Modifier.fillMaxSize()
    ) {
      items(filteredList, key = { it.id }) { item ->
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("vocab_item_${item.id}"),
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
                Text(
                  text = item.english,
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "/${item.pronunciation}/",
                  fontSize = 11.sp,
                  color = TealGlow,
                  fontWeight = FontWeight.Medium
                )
              }

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(AmberAccent.copy(alpha = 0.15f))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(
                  text = item.category,
                  color = AmberAccent,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Indonesian translation
            Text(
              text = "= ${item.indonesian}",
              fontSize = 14.sp,
              fontWeight = FontWeight.SemiBold,
              color = TealGlow
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Context Example
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF0F172A).copy(alpha = 0.5f))
                .padding(8.dp)
            ) {
              Text(
                text = "“${item.contextExample}”",
                fontSize = 12.sp,
                color = TextSecondaryDark
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Actions
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.End,
              verticalAlignment = Alignment.CenterVertically
            ) {
              IconButton(
                onClick = { viewModel.speakEnglish(item.english) },
                modifier = Modifier.size(32.dp)
              ) {
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                  contentDescription = "Audio Pengucapan",
                  tint = TealGlow,
                  modifier = Modifier.size(18.dp)
                )
              }

              IconButton(
                onClick = {
                  clipboardManager.setText(AnnotatedString("${item.english} = ${item.indonesian}"))
                  Toast.makeText(context, "Istilah disalin!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.size(32.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.ContentCopy,
                  contentDescription = "Salin Istilah",
                  tint = TextSecondaryDark,
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
