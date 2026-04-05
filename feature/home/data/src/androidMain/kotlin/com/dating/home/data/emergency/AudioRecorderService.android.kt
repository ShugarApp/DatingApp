package com.dating.home.data.emergency

import android.media.MediaRecorder
import android.os.Build

actual class AudioRecorderService actual constructor() {

    private var recorder: MediaRecorder? = null
    private var currentFilePath: String? = null
    private var recording = false

    actual fun start(filePath: String) {
        if (recording) return
        currentFilePath = filePath
        recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(android.app.Application())
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }
        recorder?.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(filePath)
            prepare()
            start()
        }
        recording = true
    }

    actual fun stop(): String? {
        if (!recording) return null
        try {
            recorder?.stop()
            recorder?.release()
        } catch (_: Exception) {
        }
        recorder = null
        recording = false
        return currentFilePath
    }

    actual fun isRecording(): Boolean = recording
}
