package com.dating.home.presentation.chat.audioplayer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URI
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import javax.sound.sampled.LineEvent

@Composable
actual fun rememberAudioPlayer(): AudioPlayer {
    val scope = rememberCoroutineScope()
    val player = remember { DesktopAudioPlayer(scope = { scope }) }

    DisposableEffect(Unit) {
        onDispose { player.release() }
    }

    return player
}

private class DesktopAudioPlayer(
    private val scope: () -> kotlinx.coroutines.CoroutineScope
) : AudioPlayer {

    private var clip: Clip? = null
    private var progressJob: Job? = null
    private var currentUrl: String? = null

    private var _isPlaying by mutableStateOf(false)
    private var _currentPositionMs by mutableLongStateOf(0L)
    private var _durationMs by mutableLongStateOf(0L)

    override val isPlaying: Boolean get() = _isPlaying
    override val currentPositionMs: Long get() = _currentPositionMs
    override val durationMs: Long get() = _durationMs

    override fun play(url: String) {
        if (currentUrl == url && clip != null) {
            resume()
            return
        }

        stop()
        currentUrl = url

        scope().launch {
            try {
                val audioStream = withContext(Dispatchers.IO) {
                    AudioSystem.getAudioInputStream(URI(url).toURL())
                }
                clip = AudioSystem.getClip().apply {
                    open(audioStream)
                    _durationMs = microsecondLength / 1000
                    addLineListener { event ->
                        if (event.type == LineEvent.Type.STOP) {
                            _isPlaying = false
                            progressJob?.cancel()
                        }
                    }
                    start()
                }
                _isPlaying = true
                startProgressTracking()
            } catch (_: Exception) {
                _isPlaying = false
            }
        }
    }

    override fun pause() {
        clip?.stop()
        _isPlaying = false
        progressJob?.cancel()
    }

    override fun resume() {
        clip?.start()
        _isPlaying = true
        startProgressTracking()
    }

    override fun stop() {
        progressJob?.cancel()
        clip?.apply {
            try {
                stop()
                close()
            } catch (_: Exception) {}
        }
        clip = null
        currentUrl = null
        _isPlaying = false
        _currentPositionMs = 0L
        _durationMs = 0L
    }

    override fun seekTo(positionMs: Long) {
        clip?.microsecondPosition = positionMs * 1000
        _currentPositionMs = positionMs
    }

    override fun release() {
        stop()
    }

    private fun startProgressTracking() {
        progressJob?.cancel()
        progressJob = scope().launch {
            while (true) {
                _currentPositionMs = (clip?.microsecondPosition ?: 0L) / 1000
                delay(200)
            }
        }
    }
}
