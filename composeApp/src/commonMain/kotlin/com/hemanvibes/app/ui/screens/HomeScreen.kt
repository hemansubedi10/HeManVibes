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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.hemanvibes.app.model.PlaybackState
import com.hemanvibes.app.model.Song
import com.hemanvibes.app.network.InnerTubeClient
import com.hemanvibes.app.player.AudioPlayer
import com.hemanvibes.app.ui.components.SongRow
import com.hemanvibes.app.ui.theme.CardDark
import com.hemanvibes.app.ui.theme.CardHighlight
import com.hemanvibes.app.ui.theme.NeonCyan
import com.hemanvibes.app.ui.theme.NeonPink
import com.hemanvibes.app.ui.theme.NeonViolet
import com.hemanvibes.app.ui.theme.TextPrimary
import com.hemanvibes.app.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    player: AudioPlayer,
    playbackState: PlaybackState,
    innerTube: InnerTubeClient,
    modifier: Modifier = Modifier
) {
    var trendingSongs by remember { mutableStateOf<List<Song>>(emptyList()) }
    var quickPicks by remember { mutableStateOf<List<Song>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val trending = innerTube.searchSongs("Top Global Hits 2026")
        val picks = innerTube.searchSongs("Synthwave Lofi Chill Vibes")
        trendingSongs = trending
        quickPicks = picks
        isLoading = false
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Hero Header
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "HEMAN VIBES",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            brush = Brush.horizontalGradient(listOf(NeonCyan, NeonPink))
                        )
                    )
                    Text(
                        text = "Stream Unlimited • Your Music, Everywhere",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Avatar / Profile badge
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(NeonCyan, NeonViolet))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "HV",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Quick Picks Carousel
        item {
            Text(
                text = "Vibe Picks For You",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = NeonCyan)
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(quickPicks.take(6)) { song ->
                        QuickPickCard(song = song) {
                            player.setQueue(quickPicks, quickPicks.indexOf(song))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
        }

        // Trending Section Header
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Whatshot, contentDescription = null, tint = NeonPink)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Trending Now",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Trending Songs List
        if (!isLoading) {
            items(trendingSongs) { song ->
                SongRow(
                    song = song,
                    isPlaying = playbackState.currentSong?.id == song.id,
                    onClick = {
                        player.setQueue(trendingSongs, trendingSongs.indexOf(song))
                    }
                )
            }
        }
    }
}

@Composable
fun QuickPickCard(song: Song, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(135.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(CardDark)
            .clickable { onClick() }
            .padding(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(115.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(CardHighlight),
            contentAlignment = Alignment.Center
        ) {
            if (song.thumbnailUrl != null) {
                AsyncImage(
                    model = song.thumbnailUrl,
                    contentDescription = song.title,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = TextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = song.title,
            color = TextPrimary,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = song.artist,
            color = TextSecondary,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
