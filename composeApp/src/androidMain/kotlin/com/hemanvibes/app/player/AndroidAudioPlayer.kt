package com.hemanvibes.app.player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
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

class AndroidAudioPlayer(private val context: Context? = null) : AudioPlayer {
    private val scope = CoroutineScope(Dispatchers.Main)
    private val _state = MutableStateFlow(PlaybackState())
    override val state: StateFlow<PlaybackState> = _state.asStateFlow()

    private var exoPlayer: ExoPlayer? = null
    private var progressJob: Job? = null

    init {
        context?.let { ctx ->
            exoPlayer = ExoPlayer.Builder(ctx).build().apply {
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        when (playbackState) {
                            Player.STATE_BUFFERING -> _state.update { it.copy(status = PlaybackStatus.BUFFERING) }
                            Player.STATE_READY -> {
                                _state.update {
                                    it.copy(
                                        status = if (playWhenReady) PlaybackStatus.PLAYING else PlaybackStatus.PAUSED,
                                        durationMs = duration.coerceAtLeast(0)
                                    )
                                }
                            }
                            Player.STATE_ENDED -> next()
                            Player.STATE_IDLE -> _state.update { it.copy(status = PlaybackStatus.IDLE) }
                        }
                    }

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        _state.update {
                            it.copy(status = if (isPlaying) PlaybackStatus.PLAYING else PlaybackStatus.PAUSED)
                        }
                    }
                })
            }
        }
    }

    override fun play(song: Song) {
        _state.update {
            it.copy(
                currentSong = song,
                status = PlaybackStatus.PLAYING,
                currentPositionMs = 0,
                durationMs = (song.durationSeconds.takeIf { d -> d > 0 } ?: 210) * 1000L
            )
        }

        song.streamUrl?.let { url ->
            exoPlayer?.let { player ->
                val mediaItem = MediaItem.fromUri(url)
                player.setMediaItem(mediaItem)
                player.prepare()
                player.play()
            }
        }
        startProgressClock()
    }

    override fun pause() {
        exoPlayer?.pause()
        _state.update { it.copy(status = PlaybackStatus.PAUSED) }
    }

    override fun resume() {
        exoPlayer?.play()
        _state.update { it.copy(status = PlaybackStatus.PLAYING) }
    }

    override fun seekTo(positionMs: Long) {
        exoPlayer?.seekTo(positionMs)
        _state.update { it.copy(currentPositionMs = positionMs) }
    }

    override fun stop() {
        exoPlayer?.stop()
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
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive && _state.value.status == PlaybackStatus.PLAYING) {
                delay(250)
                val currentPos = exoPlayer?.currentPosition ?: (_state.value.currentPositionMs + 250)
                _state.update { it.copy(currentPositionMs = currentPos) }
            }
        }
    }
}

actual fun createAudioPlayer(): AudioPlayer = AndroidAudioPlayer()
