package com.dating.home.presentation.chat.audioplayer

import android.media.MediaPlayer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
actual fun rememberAudioPlayer(): AudioPlayer {
    val scope = rememberCoroutineScope()
    val player = remember { AndroidAudioPlayer(scope = { scope }) }

    DisposableEffect(Unit) {
        onDispose { player.release() }
    }

    return player
}

private class AndroidAudioPlayer(
    private val scope: () -> kotlinx.coroutines.CoroutineScope
) : AudioPlayer {

    private var mediaPlayer: MediaPlayer? = null
    private var progressJob: Job? = null
    private var currentUrl: String? = null

    private var _isPlaying by mutableStateOf(false)
    private var _currentPositionMs by mutableLongStateOf(0L)
    private var _durationMs by mutableLongStateOf(0L)

    override val isPlaying: Boolean get() = _isPlaying
    override val currentPositionMs: Long get() = _currentPositionMs
    override val durationMs: Long get() = _durationMs

    override fun play(url: String) {
        if (currentUrl == url && mediaPlayer != null) {
            resume()
            return
        }

        stop()
        currentUrl = url
        val self = this

        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            setOnPreparedListener { mp ->
                self._durationMs = mp.duration.toLong()
                mp.start()
                self._isPlaying = true
                self.startProgressTracking()
            }
            setOnCompletionListener {
                self._isPlaying = false
                self._currentPositionMs = self._durationMs
                self.progressJob?.cancel()
            }
            setOnErrorListener { _, _, _ ->
                self._isPlaying = false
                self.progressJob?.cancel()
                true
            }
            prepareAsync()
        }
    }

    override fun pause() {
        mediaPlayer?.pause()
        _isPlaying = false
        progressJob?.cancel()
    }

    override fun resume() {
        mediaPlayer?.start()
        _isPlaying = true
        startProgressTracking()
    }

    override fun stop() {
        progressJob?.cancel()
        mediaPlayer?.apply {
            try {
                if (isPlaying) stop()
                release()
            } catch (_: Exception) {}
        }
        mediaPlayer = null
        currentUrl = null
        _isPlaying = false
        _currentPositionMs = 0L
        _durationMs = 0L
    }

    override fun seekTo(positionMs: Long) {
        mediaPlayer?.seekTo(positionMs.toInt())
        _currentPositionMs = positionMs
    }

    override fun release() {
        stop()
    }

    private fun startProgressTracking() {
        progressJob?.cancel()
        progressJob = scope().launch {
            while (true) {
                try {
                    _currentPositionMs = mediaPlayer?.currentPosition?.toLong() ?: 0L
                } catch (_: Exception) {
                    break
                }
                delay(200)
            }
        }
    }
}
