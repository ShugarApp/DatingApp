package com.dating.home.data.emergency

actual class AudioRecorderService actual constructor() {
    actual fun start(filePath: String) {}
    actual fun stop(): String? = null
    actual fun isRecording(): Boolean = false
}
