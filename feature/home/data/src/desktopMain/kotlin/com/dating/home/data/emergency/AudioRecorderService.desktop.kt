package com.dating.home.data.emergency

actual class AudioRecorderService actual constructor() {
    actual fun start(filePath: String) {
        // Audio recording not supported on desktop
    }

    actual fun stop(): String? = null

    actual fun isRecording(): Boolean = false
}
