package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.TealGlow
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TealPrimaryLight
import kotlin.math.roundToInt

@Composable
fun FloatingBubbleUi(
  isScanning: Boolean,
  onSingleTap: () -> Unit,
  modifier: Modifier = Modifier
) {
  var offsetX by remember { mutableFloatStateOf(40f) }
  var offsetY by remember { mutableFloatStateOf(160f) }

  val infiniteTransition = rememberInfiniteTransition(label = "pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = if (isScanning) 1.25f else 1.08f,
    animationSpec = infiniteRepeatable(
      animation = tween(if (isScanning) 500 else 1200),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulseScale"
  )

  Box(
    modifier = modifier
      .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
      .pointerInput(Unit) {
        detectDragGestures { change, dragAmount ->
          change.consume()
          offsetX = (offsetX + dragAmount.x).coerceIn(10f, 900f)
          offsetY = (offsetY + dragAmount.y).coerceIn(40f, 1800f)
        }
      }
      .testTag("floating_bubble_button")
  ) {
    // Outer glowing aura
    Box(
      modifier = Modifier
        .size(68.dp)
        .scale(pulseScale)
        .clip(CircleShape)
        .background(
          if (isScanning) {
            Brush.radialGradient(
              colors = listOf(AmberAccent.copy(alpha = 0.6f), Color.Transparent)
            )
          } else {
            Brush.radialGradient(
              colors = listOf(TealGlow.copy(alpha = 0.5f), Color.Transparent)
            )
          }
        )
    )

    // Inner Button
    Box(
      modifier = Modifier
        .size(60.dp)
        .align(Alignment.Center)
        .shadow(12.dp, CircleShape)
        .clip(CircleShape)
        .background(
          Brush.linearGradient(
            colors = if (isScanning) listOf(AmberAccent, Color(0xFFD97706))
            else listOf(TealPrimaryLight, TealPrimary)
          )
        )
        .border(2.dp, if (isScanning) Color.White else TealGlow, CircleShape)
        .clickable { onSingleTap() }
        .testTag("floating_bubble_tap_trigger"),
      contentAlignment = Alignment.Center
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Icon(
          imageVector = Icons.Default.Translate,
          contentDescription = "Terjemahkan Layar",
          tint = Color.White,
          modifier = Modifier.size(24.dp)
        )
        Text(
          text = if (isScanning) "SCAN..." else "EN→ID",
          color = Color.White,
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.5.sp
        )
      }
    }
  }
}
