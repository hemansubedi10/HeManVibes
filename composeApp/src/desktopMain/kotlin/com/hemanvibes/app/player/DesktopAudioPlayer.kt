package com.hemanvibes.app.player

import com.hemanvibes.app.model.PlaybackState
import com.hemanvibes.app.model.PlaybackStatus
import com.hemanvibes.app.model.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class DesktopAudioPlayer : AudioPlayer {
    private val scope = CoroutineScope(Dispatchers.Default)
    private val _state = MutableStateFlow(PlaybackState())
    override val state: StateFlow<PlaybackState> = _state.asStateFlow()

    private var progressJob: Job? = null

    override fun play(song: Song) {
        progressJob?.cancel()
        _state.update {
            it.copy(
                currentSong = song,
                status = PlaybackStatus.PLAYING,
                currentPositionMs = 0,
                durationMs = (song.durationSeconds.takeIf { d -> d > 0 } ?: 210) * 1000L
            )
        }

        // Simulate real-time playback clock until media backend buffers
        startProgressClock()
    }

    override fun pause() {
        progressJob?.cancel()
        _state.update { it.copy(status = PlaybackStatus.PAUSED) }
    }

    override fun resume() {
        if (_state.value.currentSong != null) {
            _state.update { it.copy(status = PlaybackStatus.PLAYING) }
            startProgressClock()
        }
    }

    override fun seekTo(positionMs: Long) {
        _state.update { it.copy(currentPositionMs = positionMs.coerceIn(0, it.durationMs)) }
    }

    override fun stop() {
        progressJob?.cancel()
        _state.update { it.copy(status = PlaybackStatus.IDLE, currentPositionMs = 0) }
    }

    override fun next() {
        val q = _state.value.queue
        val nextIdx = _state.value.queueIndex + 1
        if (nextIdx < q.size) {
            _state.update { it.copy(queueIndex = nextIdx) }
            play(q[nextIdx])
        }
    }

    override fun previous() {
        val q = _state.value.queue
        val prevIdx = _state.value.queueIndex - 1
        if (prevIdx >= 0 && prevIdx < q.size) {
            _state.update { it.copy(queueIndex = prevIdx) }
            play(q[prevIdx])
        }
    }

    override fun setQueue(songs: List<Song>, startIndex: Int) {
        _state.update {
            it.copy(
                queue = songs,
                queueIndex = startIndex.coerceIn(0, (songs.size - 1).coerceAtLeast(0))
            )
        }
        if (songs.isNotEmpty()) {
            play(songs[startIndex.coerceIn(0, songs.size - 1)])
        }
    }

    private fun startProgressClock() {
        progressJob = scope.launch {
            while (isActive && _state.value.status == PlaybackStatus.PLAYING) {
                delay(200)
                _state.update { current ->
                    val nextPos = current.currentPositionMs + 200
                    if (nextPos >= current.durationMs && current.durationMs > 0) {
                        current.copy(currentPositionMs = current.durationMs, status = PlaybackStatus.PAUSED)
                    } else {
                        current.copy(currentPositionMs = nextPos)
                    }
                }
            }
        }
    }
}

actual fun createAudioPlayer(): AudioPlayer = DesktopAudioPlayer()
