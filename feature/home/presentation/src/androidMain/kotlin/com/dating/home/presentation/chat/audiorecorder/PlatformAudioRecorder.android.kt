package com.dating.home.presentation.chat.audiorecorder

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import java.io.File

@Composable
actual fun rememberPlatformAudioRecorder(): PlatformAudioRecorder {
    val context = LocalContext.current
    var permissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        permissionGranted = granted
    }

    return remember(permissionGranted) {
        AndroidAudioRecorder(
            context = context,
            cacheDir = context.cacheDir,
            hasPermission = { permissionGranted },
            requestPermission = {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        )
    }
}

private class AndroidAudioRecorder(
    private val context: Context,
    private val cacheDir: File,
    private val hasPermission: () -> Boolean,
    private val requestPermission: () -> Unit
) : PlatformAudioRecorder {

    private var recorder: MediaRecorder? = null
    private var outputFile: File? = null

    override fun startRecording() {
        if (!hasPermission()) {
            requestPermission()
            return
        }

        val file = File.createTempFile("audio_record_", ".m4a", cacheDir)
        outputFile = file

        recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioSamplingRate(44100)
            setAudioEncodingBitRate(128000)
            setOutputFile(file.absolutePath)
            prepare()
            start()
        }
    }

    override fun pauseRecording() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            recorder?.pause()
        }
    }

    override fun resumeRecording() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            recorder?.resume()
        }
    }

    override fun stopRecording(): ByteArray? {
        return try {
            recorder?.apply {
                stop()
                release()
            }
            recorder = null
            outputFile?.readBytes()
        } catch (e: Exception) {
            recorder?.release()
            recorder = null
            null
        }
    }

    override fun release() {
        try {
            recorder?.release()
        } catch (_: Exception) {}
        recorder = null
        outputFile?.delete()
        outputFile = null
    }
}
