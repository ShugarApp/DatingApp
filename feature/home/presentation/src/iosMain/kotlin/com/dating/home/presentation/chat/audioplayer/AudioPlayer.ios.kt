@file:OptIn(ExperimentalForeignApi::class)

package com.dating.home.presentation.chat.audioplayer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import platform.AVFAudio.AVAudioPlayer
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL

@Composable
actual fun rememberAudioPlayer(): AudioPlayer {
    val scope = rememberCoroutineScope()
    val player = remember { IosAudioPlayer(scope = { scope }) }

    DisposableEffect(Unit) {
        onDispose { player.release() }
    }

    return player
}

private class IosAudioPlayer(
    private val scope: () -> kotlinx.coroutines.CoroutineScope
) : AudioPlayer {

    private var avPlayer: AVAudioPlayer? = null
    private var progressJob: Job? = null
    private var currentUrl: String? = null

    private var _isPlaying by mutableStateOf(false)
    private var _currentPositionMs by mutableLongStateOf(0L)
    private var _durationMs by mutableLongStateOf(0L)

    override val isPlaying: Boolean get() = _isPlaying
    override val currentPositionMs: Long get() = _currentPositionMs
    override val durationMs: Long get() = _durationMs

    override fun play(url: String) {
        if (currentUrl == url && avPlayer != null) {
            resume()
            return
        }

        stop()
        currentUrl = url

        scope().launch {
            try {
                val session = AVAudioSession.sharedInstance()
                session.setCategory(AVAudioSessionCategoryPlayback, error = null)
                session.setActive(true, error = null)

                val nsUrl = NSURL.URLWithString(url) ?: return@launch
                val data = NSData.dataWithContentsOfURL(nsUrl) ?: return@launch

                avPlayer = AVAudioPlayer(data = data, error = null)?.apply {
                    prepareToPlay()
                    _durationMs = (duration * 1000).toLong()
                    play()
                }
                _isPlaying = true
                startProgressTracking()
            } catch (_: Exception) {
                _isPlaying = false
            }
        }
    }

    override fun pause() {
        avPlayer?.pause()
        _isPlaying = false
        progressJob?.cancel()
    }

    override fun resume() {
        avPlayer?.play()
        _isPlaying = true
        startProgressTracking()
    }

    override fun stop() {
        progressJob?.cancel()
        avPlayer?.stop()
        avPlayer = null
        currentUrl = null
        _isPlaying = false
        _currentPositionMs = 0L
        _durationMs = 0L
    }

    override fun seekTo(positionMs: Long) {
        avPlayer?.currentTime = positionMs / 1000.0
        _currentPositionMs = positionMs
    }

    override fun release() {
        stop()
    }

    private fun startProgressTracking() {
        progressJob?.cancel()
        progressJob = scope().launch {
            while (true) {
                val player = avPlayer
                if (player != null) {
                    _currentPositionMs = (player.currentTime * 1000).toLong()
                    if (!player.isPlaying()) {
                        _isPlaying = false
                        _currentPositionMs = _durationMs
                        break
                    }
                }
                delay(200)
            }
        }
    }
}
