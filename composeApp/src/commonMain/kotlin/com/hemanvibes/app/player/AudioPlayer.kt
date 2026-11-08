package com.hemanvibes.app.player

import com.hemanvibes.app.model.PlaybackState
import com.hemanvibes.app.model.Song
import kotlinx.coroutines.flow.StateFlow

interface AudioPlayer {
    val state: StateFlow<PlaybackState>

    fun play(song: Song)
    fun pause()
    fun resume()
    fun seekTo(positionMs: Long)
    fun stop()
    fun next()
    fun previous()
    fun setQueue(songs: List<Song>, startIndex: Int = 0)
}

expect fun createAudioPlayer(): AudioPlayer
