package com.hemanvibes.app.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

val BgDark = Color(0xFF090714)
val SurfaceDark = Color(0xFF131024)
val CardDark = Color(0xFF1C1833)
val CardHighlight = Color(0xFF262144)

val NeonCyan = Color(0xFF00F5FF)
val NeonPurple = Color(0xFF9D4EDD)
val NeonViolet = Color(0xFF7B2CBF)
val NeonPink = Color(0xFFFF007F)

val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFF94A3B8)
val TextTertiary = Color(0xFF64748B)

val DarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color.Black,
    primaryContainer = NeonViolet,
    onPrimaryContainer = TextPrimary,
    secondary = NeonPurple,
    onSecondary = Color.Black,
    background = BgDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = CardDark,
    onSurfaceVariant = TextSecondary,
    outline = CardHighlight
)
