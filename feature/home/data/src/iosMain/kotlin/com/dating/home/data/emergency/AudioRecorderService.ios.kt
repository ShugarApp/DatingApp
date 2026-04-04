package com.dating.home.data.emergency

import platform.AVFAudio.AVAudioRecorder
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryRecord
import platform.AVFAudio.AVFormatIDKey
import platform.AVFAudio.AVNumberOfChannelsKey
import platform.AVFAudio.AVSampleRateKey
import platform.AVFAudio.kAudioFormatMPEG4AAC
import platform.CoreAudioTypes.kAudioFormatMPEG4AAC
import platform.Foundation.NSURL
import platform.darwin.Float64

actual class AudioRecorderService actual constructor() {

    private var recorder: AVAudioRecorder? = null
    private var currentFilePath: String? = null
    private var recording = false

    actual fun start(filePath: String) {
        if (recording) return
        try {
            val session = AVAudioSession.sharedInstance()
            session.setCategory(AVAudioSessionCategoryRecord, error = null)
            session.setActive(true, error = null)

            val url = NSURL.fileURLWithPath(filePath)
            val settings = mapOf<Any?, Any?>(
                AVFormatIDKey to kAudioFormatMPEG4AAC,
                AVSampleRateKey to 44100.0,
                AVNumberOfChannelsKey to 1
            )

            recorder = AVAudioRecorder(url, settings as Map<Any?, *>, null)
            recorder?.record()
            currentFilePath = filePath
            recording = true
        } catch (_: Exception) {
        }
    }

    actual fun stop(): String? {
        if (!recording) return null
        recorder?.stop()
        recorder = null
        recording = false
        AVAudioSession.sharedInstance().setActive(false, error = null)
        return currentFilePath
    }

    actual fun isRecording(): Boolean = recording
}
