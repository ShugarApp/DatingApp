package com.dating.home.presentation.chat.audioplayer

import androidx.compose.runtime.Composable

interface AudioPlayer {
    val isPlaying: Boolean
    val currentPositionMs: Long
    val durationMs: Long
    fun play(url: String)
    fun pause()
    fun resume()
    fun stop()
    fun seekTo(positionMs: Long)
    fun release()
}

@Composable
expect fun rememberAudioPlayer(): AudioPlayer
