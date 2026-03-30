package com.dating.home.presentation.chat.audiorecorder

import androidx.compose.runtime.Composable

interface PlatformAudioRecorder {
    fun startRecording()
    fun pauseRecording()
    fun resumeRecording()
    fun stopRecording(): ByteArray?
    fun release()
}

@Composable
expect fun rememberPlatformAudioRecorder(): PlatformAudioRecorder
