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
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hemanvibes.app.model.PlaybackState
import com.hemanvibes.app.player.AudioPlayer
import com.hemanvibes.app.ui.theme.CardDark
import com.hemanvibes.app.ui.theme.NeonCyan
import com.hemanvibes.app.ui.theme.NeonPink
import com.hemanvibes.app.ui.theme.NeonViolet
import com.hemanvibes.app.ui.theme.TextPrimary
import com.hemanvibes.app.ui.theme.TextSecondary

@Composable
fun LibraryScreen(
    player: AudioPlayer,
    playbackState: PlaybackState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Your Library",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            LibraryTile(
                icon = Icons.Default.Favorite,
                title = "Liked Songs",
                subtitle = "Auto-synced favorites",
                gradient = listOf(NeonPink, NeonViolet)
            ) {}
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            LibraryTile(
                icon = Icons.Default.Download,
                title = "Offline Downloads",
                subtitle = "Local cached audio (Opus / MP3)",
                gradient = listOf(NeonCyan, NeonViolet)
            ) {}
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            LibraryTile(
                icon = Icons.Default.History,
                title = "Listening History",
                subtitle = "Recently played tracks",
                gradient = listOf(NeonViolet, CardDark)
            ) {}
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            LibraryTile(
                icon = Icons.Default.PlaylistPlay,
                title = "Custom Playlists",
                subtitle = "Create & import M3U or YouTube playlists",
                gradient = listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8))
            ) {}
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardDark)
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = "HEMAN VIBES PRO STATS",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = NeonCyan
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "High Fidelity Streaming Active",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Text(
                        text = "Backend: YouTube Music InnerTube • Synced Lyrics: LRCLIB • Format: Opus @ 160 kbps",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun LibraryTile(
    icon: ImageVector,
    title: String,
    subtitle: String,
    gradient: List<Color>,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardDark)
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Brush.linearGradient(gradient)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color.White)
        }

        Spacer(modifier = Modifier.width(16.dp))

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
    }
}
