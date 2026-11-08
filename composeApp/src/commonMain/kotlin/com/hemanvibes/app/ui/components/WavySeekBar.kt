package com.hemanvibes.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.sin

/**
 * Material 3 Expressive Animated Wavy Seek Bar
 * Ripples dynamically with a smooth sine wave when music is PLAYING.
 * Becomes a sleek straight line when music is PAUSED.
 */
@Composable
fun WavySeekBar(
    progress: Float,
    isPlaying: Boolean,
    onSeek: (Float) -> Unit,
    modifier: Modifier = Modifier,
    activeColor: Color = Color(0xFF00F5FF),
    inactiveColor: Color = Color.White.copy(alpha = 0.15f),
    waveAmplitudeDp: Float = 4.5f,
    wavelengthDp: Float = 32f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "WavePhase")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isPlaying) (2 * PI).toFloat() else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WavePhaseAnimation"
    )

    var isDragging by remember { mutableStateOf(false) }
    var dragProgress by remember { mutableStateOf(progress) }

    val currentProgress = if (isDragging) dragProgress else progress

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(28.dp)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val newProgress = (offset.x / size.width).coerceIn(0f, 1f)
                    onSeek(newProgress)
                }
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = { offset ->
                        isDragging = true
                        dragProgress = (offset.x / size.width).coerceIn(0f, 1f)
                    },
                    onDragEnd = {
                        isDragging = false
                        onSeek(dragProgress)
                    },
                    onDragCancel = {
                        isDragging = false
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        val delta = dragAmount / size.width
                        dragProgress = (dragProgress + delta).coerceIn(0f, 1f)
                    }
                )
            }
    ) {
        val width = size.width
        val centerY = size.height / 2f
        val activeWidth = width * currentProgress

        val waveAmp = if (isPlaying && !isDragging) waveAmplitudeDp.dp.toPx() else 0f
        val wavelength = wavelengthDp.dp.toPx()

        // 1. Inactive Track (Right side, straight line)
        if (activeWidth < width) {
            drawLine(
                color = inactiveColor,
                start = Offset(activeWidth, centerY),
                end = Offset(width, centerY),
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        // 2. Active Track (Left side, animated wave path)
        val activePath = Path().apply {
            moveTo(0f, centerY)
            var x = 0f
            val step = 3f
            while (x <= activeWidth) {
                val y = if (waveAmp > 0f) {
                    val angle = (x / wavelength) * (2 * PI).toFloat() + wavePhase
                    centerY + sin(angle) * waveAmp
                } else {
                    centerY
                }
                lineTo(x, y)
                x += step
            }
        }

        drawPath(
            path = activePath,
            brush = Brush.horizontalGradient(
                listOf(activeColor, Color(0xFFFF007F))
            ),
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
        )

        // 3. Thumb Indicator Dot
        val thumbY = if (waveAmp > 0f) {
            val angle = (activeWidth / wavelength) * (2 * PI).toFloat() + wavePhase
            centerY + sin(angle) * waveAmp
        } else {
            centerY
        }

        // Outer glow
        drawCircle(
            color = activeColor.copy(alpha = 0.35f),
            radius = if (isDragging) 12.dp.toPx() else 8.dp.toPx(),
            center = Offset(activeWidth, thumbY)
        )

        // Solid thumb
        drawCircle(
            color = Color.White,
            radius = if (isDragging) 7.dp.toPx() else 5.dp.toPx(),
            center = Offset(activeWidth, thumbY)
        )
    }
}
