package com.dating.home.domain.emergency

interface AudioRecorderService {
    fun start(filePath: String)
    fun stop(): String?
    fun isRecording(): Boolean
}
