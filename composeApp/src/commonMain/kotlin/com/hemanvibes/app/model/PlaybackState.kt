package com.hemanvibes.app.model

import kotlinx.serialization.Serializable

@Serializable
data class LyricLine(
    val timestampMs: Long,
    val text: String
)

@Serializable
data class Lyrics(
    val trackName: String,
    val artistName: String,
    val durationSeconds: Double,
    val plainLyrics: String? = null,
    val syncedLyrics: List<LyricLine> = emptyList()
) {
    val isSynced: Boolean get() = syncedLyrics.isNotEmpty()
}

enum class PlaybackStatus {
    IDLE,
    BUFFERING,
    PLAYING,
    PAUSED,
    ERROR
}

data class PlaybackState(
    val currentSong: Song? = null,
    val status: PlaybackStatus = PlaybackStatus.IDLE,
    val currentPositionMs: Long = 0,
    val durationMs: Long = 0,
    val volume: Float = 1.0f,
    val isRepeat: Boolean = false,
    val isShuffle: Boolean = false,
    val queue: List<Song> = emptyList(),
    val queueIndex: Int = 0,
    val errorMessage: String? = null
) {
    val progress: Float
        get() = if (durationMs > 0) (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f
}
