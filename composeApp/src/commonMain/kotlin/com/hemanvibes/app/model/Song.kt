package com.hemanvibes.app.model

import kotlinx.serialization.Serializable

@Serializable
data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val album: String? = null,
    val durationSeconds: Long = 0,
    val thumbnailUrl: String? = null,
    val streamUrl: String? = null,
    val isLiked: Boolean = false
) {
    val formattedDuration: String
        get() {
            val minutes = durationSeconds / 60
            val seconds = durationSeconds % 60
            return "$minutes:${seconds.toString().padStart(2, '0')}"
        }
}

@Serializable
data class Playlist(
    val id: String,
    val title: String,
    val author: String,
    val songCount: Int,
    val thumbnailUrl: String? = null
)
