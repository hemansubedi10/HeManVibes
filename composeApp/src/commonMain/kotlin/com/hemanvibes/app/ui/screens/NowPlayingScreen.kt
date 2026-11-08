package com.hemanvibes.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.hemanvibes.app.model.Lyrics
import com.hemanvibes.app.model.PlaybackState
import com.hemanvibes.app.model.PlaybackStatus
import com.hemanvibes.app.network.LyricsClient
import com.hemanvibes.app.player.AudioPlayer
import com.hemanvibes.app.ui.components.MeshBackdrop
import com.hemanvibes.app.ui.components.SongRow
import com.hemanvibes.app.ui.components.WavySeekBar
import com.hemanvibes.app.ui.theme.CardDark
import com.hemanvibes.app.ui.theme.NeonCyan
import com.hemanvibes.app.ui.theme.NeonPink
import com.hemanvibes.app.ui.theme.TextPrimary
import com.hemanvibes.app.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingScreen(
    state: PlaybackState,
    player: AudioPlayer,
    lyricsClient: LyricsClient,
    onDismiss: () -> Unit
) {
    val song = state.currentSong ?: return
    var showLyrics by remember { mutableStateOf(false) }
    var showQueue by remember { mutableStateOf(false) }
    var showSleepTimer by remember { mutableStateOf(false) }
    var activeTimerMinutes by remember { mutableStateOf<Int?>(null) }
    var lyrics by remember { mutableStateOf<Lyrics?>(null) }
    var isLiked by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    LaunchedEffect(song.id) {
        lyrics = lyricsClient.getLyrics(song.title, song.artist)
    }

    // Auto-scroll synced karaoke lyrics
    val currentLineIndex = lyrics?.syncedLyrics?.indexOfLast { it.timestampMs <= state.currentPositionMs } ?: -1
    LaunchedEffect(currentLineIndex) {
        if (showLyrics && currentLineIndex >= 0) {
            listState.animateScrollToItem((currentLineIndex - 2).coerceAtLeast(0))
        }
    }

    val isPlaying = state.status == PlaybackStatus.PLAYING

    Box(modifier = Modifier.fillMaxSize()) {
        // Fluid Ambient Mesh Backdrop (Apple Music / SimpMusic style)
        MeshBackdrop(isPlaying = isPlaying)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Top Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = "Collapse",
                        tint = TextPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Text(
                    text = if (showLyrics) "KARAOKE LYRICS" else "HEMAN VIBES EXPRESSIVE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    ),
                    color = NeonCyan
                )

                Row {
                    IconButton(onClick = { showSleepTimer = true }) {
                        Icon(
                            Icons.Default.Bedtime,
                            contentDescription = "Sleep Timer",
                            tint = if (activeTimerMinutes != null) NeonCyan else TextSecondary
                        )
                    }
                    IconButton(onClick = { showLyrics = !showLyrics }) {
                        Icon(
                            Icons.Default.Lyrics,
                            contentDescription = "Lyrics",
                            tint = if (showLyrics) NeonPink else TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Center Display: Artwork OR Synced Lyrics
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (showLyrics && lyrics != null && lyrics!!.isSynced) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        itemsIndexed(lyrics!!.syncedLyrics) { idx, line ->
                            val isCurrent = idx == currentLineIndex
                            Text(
                                text = line.text,
                                style = if (isCurrent) {
                                    MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        brush = Brush.horizontalGradient(listOf(NeonCyan, NeonPink))
                                    )
                                } else {
                                    MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
                                },
                                color = if (isCurrent) TextPrimary else TextSecondary.copy(alpha = 0.45f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { player.seekTo(line.timestampMs) }
                                    .padding(vertical = 4.dp)
                            )
                        }
                    }
                } else {
                    // Expressive Squircle Vinyl Artwork
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(32.dp))
                            .background(CardDark),
                        contentAlignment = Alignment.Center
                    ) {
                        if (song.thumbnailUrl != null) {
                            AsyncImage(
                                model = song.thumbnailUrl,
                                contentDescription = song.title,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(72.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Track Info & Favorite Heart
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = song.title,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${song.artist} • ${song.album ?: "Single"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(onClick = { isLiked = !isLiked }) {
                    Icon(
                        imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isLiked) NeonPink else TextSecondary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Animated Material 3 Expressive Wavy Seek Bar!
            WavySeekBar(
                progress = state.progress,
                isPlaying = isPlaying,
                onSeek = { targetProgress ->
                    val targetMs = (targetProgress * state.durationMs).toLong()
                    player.seekTo(targetMs)
                }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatMs(state.currentPositionMs),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Text(
                    text = formatMs(state.durationMs),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Transport Control Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* Shuffle */ }) {
                    Icon(Icons.Default.Shuffle, contentDescription = "Shuffle", tint = TextSecondary)
                }

                IconButton(onClick = { player.previous() }) {
                    Icon(
                        Icons.Default.SkipPrevious,
                        contentDescription = "Previous",
                        tint = TextPrimary,
                        modifier = Modifier.size(38.dp)
                    )
                }

                // Glowing Neon Play/Pause Pulsar
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(NeonCyan, NeonPink))
                        )
                        .clickable {
                            if (isPlaying) player.pause() else player.resume()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = Color.Black,
                        modifier = Modifier.size(40.dp)
                    )
                }

                IconButton(onClick = { player.next() }) {
                    Icon(
                        Icons.Default.SkipNext,
                        contentDescription = "Next",
                        tint = TextPrimary,
                        modifier = Modifier.size(38.dp)
                    )
                }

                IconButton(onClick = { showQueue = true }) {
                    Icon(
                        Icons.Default.QueueMusic,
                        contentDescription = "Up Next Queue",
                        tint = TextSecondary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))
        }

        // Up Next Queue Sheet
        if (showQueue) {
            ModalBottomSheet(
                onDismissRequest = { showQueue = false },
                sheetState = rememberModalBottomSheetState(),
                containerColor = CardDark
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Up Next Queue (${state.queue.size} songs)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(modifier = Modifier.fillMaxWidth().height(360.dp)) {
                        itemsIndexed(state.queue) { idx, qSong ->
                            SongRow(
                                song = qSong,
                                isPlaying = state.queueIndex == idx,
                                onClick = {
                                    player.setQueue(state.queue, idx)
                                    showQueue = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Sleep Timer Sheet
        if (showSleepTimer) {
            ModalBottomSheet(
                onDismissRequest = { showSleepTimer = false },
                sheetState = rememberModalBottomSheetState(),
                containerColor = CardDark
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = "Sleep Timer",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    listOf(15, 30, 45, 60).forEach { mins ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    activeTimerMinutes = mins
                                    showSleepTimer = false
                                }
                                .padding(vertical = 14.dp, horizontal = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$mins minutes",
                                color = if (activeTimerMinutes == mins) NeonCyan else TextPrimary,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                            )
                            if (activeTimerMinutes == mins) {
                                Text("Active", color = NeonCyan, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }

                    if (activeTimerMinutes != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Turn off timer",
                            color = NeonPink,
                            modifier = Modifier
                                .clickable {
                                    activeTimerMinutes = null
                                    showSleepTimer = false
                                }
                                .padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

private fun formatMs(ms: Long): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "$minutes:${seconds.toString().padStart(2, '0')}"
}
