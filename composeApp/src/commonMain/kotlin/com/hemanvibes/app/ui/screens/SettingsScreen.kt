package com.hemanvibes.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hemanvibes.app.ui.theme.CardDark
import com.hemanvibes.app.ui.theme.CardHighlight
import com.hemanvibes.app.ui.theme.NeonCyan
import com.hemanvibes.app.ui.theme.NeonPink
import com.hemanvibes.app.ui.theme.TextPrimary
import com.hemanvibes.app.ui.theme.TextSecondary

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    var highQualityAudio by remember { mutableStateOf(true) }
    var amoledBlack by remember { mutableStateOf(true) }
    var wavyWaveform by remember { mutableStateOf(true) }
    var selectedPlayerStyle by remember { mutableStateOf("Apple Music Ambient Mesh") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Settings & Audio Engine",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Section: Audio Quality & Streaming
        item {
            SettingsCategoryHeader(title = "AUDIO PLAYBACK & QUALITY", icon = Icons.Default.GraphicEq)
            Spacer(modifier = Modifier.height(8.dp))

            SettingsToggleRow(
                title = "Maximum Quality (Opus @ 160 kbps)",
                subtitle = "Streams YouTube Music's highest fidelity audio stream",
                checked = highQualityAudio,
                onCheckedChange = { highQualityAudio = it }
            )
            Spacer(modifier = Modifier.height(8.dp))

            SettingsToggleRow(
                title = "Material 3 Expressive Wavy Seekbar",
                subtitle = "Animated fluid sine-wave progress bar when music is playing",
                checked = wavyWaveform,
                onCheckedChange = { wavyWaveform = it }
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Section: Interface & Theme
        item {
            SettingsCategoryHeader(title = "INTERFACE & PLAYER STYLES", icon = Icons.Default.Palette)
            Spacer(modifier = Modifier.height(8.dp))

            SettingsToggleRow(
                title = "Pure Obsidian AMOLED Dark",
                subtitle = "Saves battery on OLED displays and gives vibrant neon contrast",
                checked = amoledBlack,
                onCheckedChange = { amoledBlack = it }
            )
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(CardDark)
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "Fullscreen Player Style",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Text(
                        text = "Active: $selectedPlayerStyle",
                        style = MaterialTheme.typography.bodySmall,
                        color = NeonCyan
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Section: Storage & Cache
        item {
            SettingsCategoryHeader(title = "CACHE & OFFLINE DATA", icon = Icons.Default.CleaningServices)
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(CardDark)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Clear Audio & Artwork Cache",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = "Currently using 24.8 MB of disk storage",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                    Text(
                        text = "Clean",
                        color = NeonPink,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Section: About HeMan Vibes
        item {
            SettingsCategoryHeader(title = "ABOUT HEMan VIBES", icon = Icons.Default.Info)
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(CardDark)
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "HeMan Vibes v1.0.0",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Text(
                        text = "Developed by Heman Subedi (@hemansubedi10)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NeonCyan
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Architecture: Compose Multiplatform • InnerTube • LRCLIB • ExoPlayer / AVPlayer",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsCategoryHeader(title: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = NeonCyan
        )
    }
}

@Composable
fun SettingsToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardDark)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = NeonCyan,
                uncheckedThumbColor = TextSecondary,
                uncheckedTrackColor = CardHighlight
            )
        )
    }
}
