@file:OptIn(ExperimentalForeignApi::class)

package com.dating.home.presentation.chat.audiorecorder

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.refTo
import platform.AVFAudio.AVAudioRecorder
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryRecord
import platform.AVFAudio.AVAudioSessionSetActiveOptionNotifyOthersOnDeactivation
import platform.AVFAudio.AVEncoderBitRateKey
import platform.AVFAudio.AVFormatIDKey
import platform.AVFAudio.AVNumberOfChannelsKey
import platform.AVFAudio.AVSampleRateKey
import platform.CoreAudioTypes.kAudioFormatMPEG4AAC
import platform.Foundation.NSDate
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSData
import platform.Foundation.dataWithContentsOfURL
import platform.Foundation.timeIntervalSince1970
import platform.posix.memcpy

@Composable
actual fun rememberPlatformAudioRecorder(): PlatformAudioRecorder {
    return remember { IosAudioRecorder() }
}

private class IosAudioRecorder : PlatformAudioRecorder {

    private var recorder: AVAudioRecorder? = null
    private var fileUrl: NSURL? = null

    override fun startRecording() {
        val session = AVAudioSession.sharedInstance()
        session.setCategory(AVAudioSessionCategoryRecord, error = null)
        session.setActive(true, error = null)

        val fileName = "audio_record_${NSDate().timeIntervalSince1970}.m4a"
        val url = NSURL.fileURLWithPath(NSTemporaryDirectory() + fileName)
        fileUrl = url

        val settings = mapOf<Any?, Any?>(
            AVFormatIDKey to kAudioFormatMPEG4AAC,
            AVSampleRateKey to 44100.0,
            AVNumberOfChannelsKey to 1,
            AVEncoderBitRateKey to 128000
        )

        recorder = AVAudioRecorder(uRL = url, settings = settings, error = null)
        recorder?.record()
    }

    override fun pauseRecording() {
        recorder?.pause()
    }

    override fun resumeRecording() {
        recorder?.record()
    }

    override fun stopRecording(): ByteArray? {
        recorder?.stop()
        recorder = null

        val session = AVAudioSession.sharedInstance()
        session.setActive(
            false,
            withOptions = AVAudioSessionSetActiveOptionNotifyOthersOnDeactivation,
            error = null
        )

        val url = fileUrl ?: return null
        val data = NSData.dataWithContentsOfURL(url) ?: return null

        val bytes = ByteArray(data.length.toInt())
        if (bytes.isNotEmpty()) {
            memcpy(bytes.refTo(0), data.bytes, data.length)
        }
        return bytes
    }

    override fun release() {
        recorder?.stop()
        recorder = null
        fileUrl = null
    }
}
