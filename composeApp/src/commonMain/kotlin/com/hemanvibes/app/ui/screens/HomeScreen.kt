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
import androidx.compose.material.icons.filled.AutoAwesome
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
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    player: AudioPlayer,
    playbackState: PlaybackState,
    innerTube: InnerTubeClient,
    modifier: Modifier = Modifier
) {
    val moods = listOf("Chill Vibe", "Workout", "Focus & Study", "Phonk", "Synthwave", "Party & EDM", "Lofi Beats")
    var selectedMood by remember { mutableStateOf(moods[0]) }

    var trendingSongs by remember { mutableStateOf<List<Song>>(emptyList()) }
    var quickPicks by remember { mutableStateOf<List<Song>>(emptyList()) }
    var moodSongs by remember { mutableStateOf<List<Song>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        trendingSongs = innerTube.searchSongs("Top Global Hits 2026")
        quickPicks = innerTube.searchSongs("The Weeknd Daft Punk Dua Lipa")
        moodSongs = innerTube.searchSongs(selectedMood)
        isLoading = false
    }

    fun onMoodSelected(mood: String) {
        selectedMood = mood
        scope.launch {
            moodSongs = innerTube.searchSongs(mood)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // App Header
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
                        text = "Hi HeMan • Stream Unlimited Ad-Free",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Profile Avatar Badge
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(NeonCyan, NeonViolet))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "HV",
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Mood & Activity Chips Bar
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(moods) { mood ->
                    val isSelected = mood == selectedMood
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isSelected) {
                                    Brush.horizontalGradient(listOf(NeonCyan, NeonViolet))
                                } else {
                                    Brush.linearGradient(listOf(CardDark, CardDark))
                                }
                            )
                            .clickable { onMoodSelected(mood) }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = mood,
                            color = if (isSelected) Color.Black else TextPrimary,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Quick Picks Carousel
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = NeonCyan)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Speed Dial Picks",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = NeonCyan)
                }
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    items(quickPicks.take(6)) { song ->
                        QuickPickCard(song = song) {
                            player.setQueue(quickPicks, quickPicks.indexOf(song))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
        }

        // Selected Mood Song Collection
        item {
            Text(
                text = "Vibes For: $selectedMood",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(moodSongs.take(5)) { song ->
            SongRow(
                song = song,
                isPlaying = playbackState.currentSong?.id == song.id,
                onClick = { player.setQueue(moodSongs, moodSongs.indexOf(song)) }
            )
        }

        // Trending Header
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Whatshot, contentDescription = null, tint = NeonPink)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Global Trending Charts",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(trendingSongs) { song ->
            SongRow(
                song = song,
                isPlaying = playbackState.currentSong?.id == song.id,
                onClick = { player.setQueue(trendingSongs, trendingSongs.indexOf(song)) }
            )
        }
    }
}

@Composable
fun QuickPickCard(song: Song, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(135.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(CardDark)
            .clickable { onClick() }
            .padding(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(115.dp)
                .clip(RoundedCornerShape(10.dp))
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
