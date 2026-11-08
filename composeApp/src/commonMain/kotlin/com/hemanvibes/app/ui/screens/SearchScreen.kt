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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.NorthWest
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hemanvibes.app.model.PlaybackState
import com.hemanvibes.app.model.Song
import com.hemanvibes.app.network.InnerTubeClient
import com.hemanvibes.app.player.AudioPlayer
import com.hemanvibes.app.ui.components.SongRow
import com.hemanvibes.app.ui.theme.CardDark
import com.hemanvibes.app.ui.theme.CardHighlight
import com.hemanvibes.app.ui.theme.NeonCyan
import com.hemanvibes.app.ui.theme.TextPrimary
import com.hemanvibes.app.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(
    player: AudioPlayer,
    playbackState: PlaybackState,
    innerTube: InnerTubeClient,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<Song>>(emptyList()) }
    var suggestions by remember { mutableStateOf<List<String>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    var showSuggestions by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val chips = listOf("Phonk", "Lofi Chill", "Synthwave", "Hip Hop", "EDM", "Anime OST", "Metal", "Nepali Hits")

    // Debounced search suggestions query
    LaunchedEffect(searchQuery) {
        if (searchQuery.length >= 2 && showSuggestions) {
            delay(250)
            suggestions = innerTube.getSearchSuggestions(searchQuery)
        } else {
            suggestions = emptyList()
        }
    }

    fun performSearch(query: String) {
        if (query.isBlank()) return
        searchQuery = query
        showSuggestions = false
        isSearching = true
        scope.launch {
            searchResults = innerTube.searchSongs(query)
            isSearching = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Search Input Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                showSuggestions = true
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search songs, artists, albums...", color = TextSecondary) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = NeonCyan)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = {
                        searchQuery = ""
                        searchResults = emptyList()
                        suggestions = emptyList()
                        showSuggestions = false
                    }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary)
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CardDark,
                unfocusedContainerColor = CardDark,
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = CardHighlight,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = NeonCyan
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Live YouTube Search Suggestions Dropdown
        if (showSuggestions && suggestions.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardDark)
                    .padding(8.dp)
            ) {
                suggestions.take(5).forEach { sug ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { performSearch(sug) }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = sug, color = TextPrimary, style = MaterialTheme.typography.bodyMedium)
                        }
                        Icon(Icons.Default.NorthWest, contentDescription = "Use", tint = TextSecondary, modifier = Modifier.size(16.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Quick Category Chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(chips) { chip ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (searchQuery == chip) NeonCyan else CardDark)
                        .clickable { performSearch(chip) }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = chip,
                        color = if (searchQuery == chip) Color.Black else TextPrimary,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Results or Loading
        if (isSearching) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NeonCyan)
            }
        } else if (searchResults.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                items(searchResults) { song ->
                    SongRow(
                        song = song,
                        isPlaying = playbackState.currentSong?.id == song.id,
                        onClick = {
                            player.setQueue(searchResults, searchResults.indexOf(song))
                        }
                    )
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = if (searchQuery.isEmpty()) "Search millions of songs on YouTube Music" else "No songs found",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
