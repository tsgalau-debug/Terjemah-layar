package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TranslationBlock
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.TealGlow
import com.example.ui.theme.TealPrimary
import kotlin.math.roundToInt

@Composable
fun InPlaceOverlay(
  blocks: List<TranslationBlock>,
  selectedBlockId: String?,
  overlayOpacity: Float,
  onBlockClick: (TranslationBlock) -> Unit,
  modifier: Modifier = Modifier
) {
  BoxWithConstraints(modifier = modifier.fillMaxSize()) {
    val totalWidth = maxWidth
    val totalHeight = maxHeight

    blocks.forEach { block ->
      val isSelected = block.id == selectedBlockId

      val xOffsetPx = (totalWidth.value * block.xRatio).dp
      val yOffsetPx = (totalHeight.value * block.yRatio).dp
      val blockWidth = (totalWidth.value * block.widthRatio).dp

      AnimatedVisibility(
        visible = true,
        enter = fadeIn() + slideInVertically { it / 4 },
        modifier = Modifier.offset(x = xOffsetPx, y = yOffsetPx)
      ) {
        Box(
          modifier = Modifier
            .width(blockWidth)
            .shadow(if (isSelected) 8.dp else 4.dp, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(
              if (isSelected) Color(0xFF0F172A).copy(alpha = overlayOpacity.coerceAtLeast(0.92f))
              else Color(0xFF0F172A).copy(alpha = overlayOpacity)
            )
            .border(
              width = if (isSelected) 2.dp else 1.dp,
              color = if (isSelected) AmberAccent else TealGlow.copy(alpha = 0.7f),
              shape = RoundedCornerShape(8.dp)
            )
            .clickable { onBlockClick(block) }
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .testTag("inplace_overlay_block_${block.id}")
        ) {
          Column {
            Row(
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = if (isSelected) AmberAccent else TealGlow,
                modifier = Modifier
                  .size(11.dp)
                  .padding(end = 3.dp)
              )
              Text(
                text = "ID: ${block.category}",
                color = if (isSelected) AmberAccent else TealGlow,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
              )
            }

            Text(
              text = block.translatedText,
              color = Color.White,
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold,
              lineHeight = 17.sp,
              maxLines = 3,
              overflow = TextOverflow.Ellipsis
            )
          }
        }
      }
    }
  }
}
