package com.hemanvibes.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hemanvibes.app.network.InnerTubeClient
import com.hemanvibes.app.network.LyricsClient
import com.hemanvibes.app.player.createAudioPlayer
import com.hemanvibes.app.ui.components.MiniPlayer
import com.hemanvibes.app.ui.screens.HomeScreen
import com.hemanvibes.app.ui.screens.LibraryScreen
import com.hemanvibes.app.ui.screens.NowPlayingScreen
import com.hemanvibes.app.ui.screens.SearchScreen
import com.hemanvibes.app.ui.theme.BgDark
import com.hemanvibes.app.ui.theme.CardDark
import com.hemanvibes.app.ui.theme.HeManVibesTheme
import com.hemanvibes.app.ui.theme.NeonCyan
import com.hemanvibes.app.ui.theme.TextPrimary
import com.hemanvibes.app.ui.theme.TextSecondary

enum class AppTab {
    HOME, SEARCH, LIBRARY
}

@Composable
fun App() {
    HeManVibesTheme {
        val player = remember { createAudioPlayer() }
        val innerTube = remember { InnerTubeClient() }
        val lyricsClient = remember { LyricsClient() }
        val playbackState by player.state.collectAsState()

        var currentTab by remember { mutableStateOf(AppTab.HOME) }
        var isPlayerExpanded by remember { mutableStateOf(false) }

        BoxWithConstraints(modifier = Modifier.fillMaxSize().background(BgDark)) {
            val isDesktop = maxWidth > 768.dp

            if (isDesktop) {
                // Desktop / Landscape Layout with Side Navigation Rail
                Row(modifier = Modifier.fillMaxSize()) {
                    NavigationRail(
                        containerColor = CardDark,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        NavigationRailItem(
                            selected = currentTab == AppTab.HOME,
                            onClick = { currentTab = AppTab.HOME },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                            label = { Text("Home") },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = NeonCyan,
                                selectedTextColor = NeonCyan,
                                indicatorColor = CardDark,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary
                            )
                        )
                        NavigationRailItem(
                            selected = currentTab == AppTab.SEARCH,
                            onClick = { currentTab = AppTab.SEARCH },
                            icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                            label = { Text("Search") },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = NeonCyan,
                                selectedTextColor = NeonCyan,
                                indicatorColor = CardDark,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary
                            )
                        )
                        NavigationRailItem(
                            selected = currentTab == AppTab.LIBRARY,
                            onClick = { currentTab = AppTab.LIBRARY },
                            icon = { Icon(Icons.Default.LibraryMusic, contentDescription = "Library") },
                            label = { Text("Library") },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = NeonCyan,
                                selectedTextColor = NeonCyan,
                                indicatorColor = CardDark,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary
                            )
                        )
                    }

                    Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                        Box(modifier = Modifier.weight(1f)) {
                            when (currentTab) {
                                AppTab.HOME -> HomeScreen(player, playbackState, innerTube)
                                AppTab.SEARCH -> SearchScreen(player, playbackState, innerTube)
                                AppTab.LIBRARY -> LibraryScreen(player, playbackState)
                            }
                        }

                        // Persistent bottom player for Desktop
                        if (playbackState.currentSong != null) {
                            MiniPlayer(
                                state = playbackState,
                                player = player,
                                onExpand = { isPlayerExpanded = true }
                            )
                        }
                    }
                }
            } else {
                // Mobile Layout with Bottom Navigation Bar
                Scaffold(
                    containerColor = BgDark,
                    bottomBar = {
                        Column {
                            if (playbackState.currentSong != null) {
                                MiniPlayer(
                                    state = playbackState,
                                    player = player,
                                    onExpand = { isPlayerExpanded = true }
                                )
                            }
                            NavigationBar(containerColor = CardDark) {
                                NavigationBarItem(
                                    selected = currentTab == AppTab.HOME,
                                    onClick = { currentTab = AppTab.HOME },
                                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                    label = { Text("Home") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = NeonCyan,
                                        selectedTextColor = NeonCyan,
                                        indicatorColor = CardDark,
                                        unselectedIconColor = TextSecondary,
                                        unselectedTextColor = TextSecondary
                                    )
                                )
                                NavigationBarItem(
                                    selected = currentTab == AppTab.SEARCH,
                                    onClick = { currentTab = AppTab.SEARCH },
                                    icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                                    label = { Text("Search") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = NeonCyan,
                                        selectedTextColor = NeonCyan,
                                        indicatorColor = CardDark,
                                        unselectedIconColor = TextSecondary,
                                        unselectedTextColor = TextSecondary
                                    )
                                )
                                NavigationBarItem(
                                    selected = currentTab == AppTab.LIBRARY,
                                    onClick = { currentTab = AppTab.LIBRARY },
                                    icon = { Icon(Icons.Default.LibraryMusic, contentDescription = "Library") },
                                    label = { Text("Library") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = NeonCyan,
                                        selectedTextColor = NeonCyan,
                                        indicatorColor = CardDark,
                                        unselectedIconColor = TextSecondary,
                                        unselectedTextColor = TextSecondary
                                    )
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                        when (currentTab) {
                            AppTab.HOME -> HomeScreen(player, playbackState, innerTube)
                            AppTab.SEARCH -> SearchScreen(player, playbackState, innerTube)
                            AppTab.LIBRARY -> LibraryScreen(player, playbackState)
                        }
                    }
                }
            }

            // Fullscreen Now Playing overlay
            AnimatedVisibility(
                visible = isPlayerExpanded,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                NowPlayingScreen(
                    state = playbackState,
                    player = player,
                    lyricsClient = lyricsClient,
                    onDismiss = { isPlayerExpanded = false }
                )
            }
        }
    }
}
