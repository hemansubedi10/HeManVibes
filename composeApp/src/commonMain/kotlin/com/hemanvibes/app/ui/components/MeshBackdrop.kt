package com.hemanvibes.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.hemanvibes.app.ui.theme.BgDark
import com.hemanvibes.app.ui.theme.NeonCyan
import com.hemanvibes.app.ui.theme.NeonPink
import com.hemanvibes.app.ui.theme.NeonViolet
import kotlin.math.cos
import kotlin.math.sin

/**
 * Animated Ambient Mesh Gradient Backdrop
 * Simulates the fluid Apple Music / SimpMusic immersive album backdrop
 */
@Composable
fun MeshBackdrop(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    primaryGlow: Color = NeonViolet,
    secondaryGlow: Color = NeonCyan,
    accentGlow: Color = NeonPink
) {
    val infiniteTransition = rememberInfiniteTransition(label = "MeshMotion")
    val animTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isPlaying) 6.28f else 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "MeshTime"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Solid Dark Base
        drawRect(color = BgDark)

        // Orb 1 (Top Left, Purple/Violet)
        val orb1X = w * (0.35f + 0.15f * sin(animTime))
        val orb1Y = h * (0.30f + 0.10f * cos(animTime))
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(primaryGlow.copy(alpha = 0.40f), Color.Transparent),
                center = Offset(orb1X, orb1Y),
                radius = w * 0.7f
            ),
            radius = w * 0.7f,
            center = Offset(orb1X, orb1Y)
        )

        // Orb 2 (Right Mid, Cyan)
        val orb2X = w * (0.75f - 0.12f * cos(animTime * 0.8f))
        val orb2Y = h * (0.55f + 0.15f * sin(animTime * 0.8f))
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(secondaryGlow.copy(alpha = 0.28f), Color.Transparent),
                center = Offset(orb2X, orb2Y),
                radius = w * 0.65f
            ),
            radius = w * 0.65f,
            center = Offset(orb2X, orb2Y)
        )

        // Orb 3 (Bottom Left, Magenta Pink)
        val orb3X = w * (0.25f + 0.10f * cos(animTime * 1.2f))
        val orb3Y = h * (0.80f - 0.12f * sin(animTime * 1.2f))
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(accentGlow.copy(alpha = 0.30f), Color.Transparent),
                center = Offset(orb3X, orb3Y),
                radius = w * 0.6f
            ),
            radius = w * 0.6f,
            center = Offset(orb3X, orb3Y)
        )

        // Dark Vignette Overlay
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    BgDark.copy(alpha = 0.25f),
                    BgDark.copy(alpha = 0.60f),
                    BgDark.copy(alpha = 0.95f)
                )
            )
        )
    }
}
