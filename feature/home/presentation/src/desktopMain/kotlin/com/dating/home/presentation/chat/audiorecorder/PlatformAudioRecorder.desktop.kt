package com.dating.home.presentation.chat.audiorecorder

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.io.ByteArrayOutputStream
import java.io.File
import javax.sound.sampled.AudioFileFormat
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.TargetDataLine

@Composable
actual fun rememberPlatformAudioRecorder(): PlatformAudioRecorder {
    return remember { DesktopAudioRecorder() }
}

private class DesktopAudioRecorder : PlatformAudioRecorder {

    private var recordingThread: Thread? = null
    private var targetLine: TargetDataLine? = null
    private var tempFile: File? = null

    @Volatile
    private var isCurrentlyRecording = false

    override fun startRecording() {
        val format = AudioFormat(44100f, 16, 1, true, false)
        val line = AudioSystem.getTargetDataLine(format)
        targetLine = line

        val file = File.createTempFile("audio_record_", ".wav")
        tempFile = file

        line.open(format)
        line.start()
        isCurrentlyRecording = true

        recordingThread = Thread {
            try {
                val audioStream = javax.sound.sampled.AudioInputStream(line)
                AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, file)
            } catch (_: Exception) {}
        }.apply {
            isDaemon = true
            start()
        }
    }

    override fun pauseRecording() {
        targetLine?.stop()
    }

    override fun resumeRecording() {
        targetLine?.start()
    }

    override fun stopRecording(): ByteArray? {
        isCurrentlyRecording = false
        targetLine?.stop()
        targetLine?.close()
        targetLine = null
        recordingThread?.join(2000)
        recordingThread = null

        val file = tempFile ?: return null
        return try {
            file.readBytes()
        } catch (_: Exception) {
            null
        }
    }

    override fun release() {
        isCurrentlyRecording = false
        targetLine?.stop()
        targetLine?.close()
        targetLine = null
        recordingThread = null
        tempFile?.delete()
        tempFile = null
    }
}
