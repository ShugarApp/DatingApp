package com.dating.home.data.emergency

expect class AudioRecorderService() {
    fun start(filePath: String)
    fun stop(): String?
    fun isRecording(): Boolean
}
