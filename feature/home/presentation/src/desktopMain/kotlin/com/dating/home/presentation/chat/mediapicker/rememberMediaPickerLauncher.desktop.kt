package com.dating.home.presentation.chat.mediapicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.dating.home.presentation.profile.mediapicker.PickedImageData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.io.FilenameFilter
import java.nio.file.Files
import javax.swing.SwingUtilities
import kotlin.coroutines.resume

private val imageExtensions = listOf("png", "jpg", "jpeg", "webp", "gif")
private val audioExtensions = listOf("aac", "mp4", "mp3", "ogg", "wav")

private fun getMimeTypeFromFileName(fileName: String): String? {
    val extension = fileName.substringAfterLast(".", "").lowercase()
    return when (extension) {
        "png" -> "image/png"
        "jpg", "jpeg" -> "image/jpeg"
        "webp" -> "image/webp"
        "gif" -> "image/gif"
        "aac" -> "audio/aac"
        "mp4" -> "audio/mp4"
        "mp3" -> "audio/mpeg"
        "ogg" -> "audio/ogg"
        "wav" -> "audio/wav"
        else -> null
    }
}

@Composable
actual fun rememberMediaPickerLauncher(
    mediaFilter: MediaFilter,
    onResult: (PickedImageData) -> Unit
): MediaPickerLauncher {
    val scope = rememberCoroutineScope()

    return remember(mediaFilter) {
        MediaPickerLauncher(
            onLaunch = {
                scope.launch {
                    pickFile(mediaFilter)?.let { data ->
                        onResult(data)
                    }
                }
            }
        )
    }
}

private suspend fun pickFile(mediaFilter: MediaFilter): PickedImageData? {
    val allowedExtensions = when (mediaFilter) {
        MediaFilter.IMAGES_AND_GIFS -> imageExtensions
        MediaFilter.AUDIO -> audioExtensions
    }

    val title = when (mediaFilter) {
        MediaFilter.IMAGES_AND_GIFS -> "Select image"
        MediaFilter.AUDIO -> "Select audio"
    }

    val file = suspendCancellableCoroutine<File?> { continuation ->
        var fileDialog: FileDialog? = null

        continuation.invokeOnCancellation {
            SwingUtilities.invokeLater {
                fileDialog?.dispose()
            }
        }

        SwingUtilities.invokeLater {
            try {
                fileDialog = FileDialog(Frame(), title, FileDialog.LOAD)
                fileDialog.filenameFilter = FilenameFilter { _, name ->
                    allowedExtensions.any { name.lowercase().endsWith(".$it") }
                }
                fileDialog.isVisible = true

                val selectedFile = if (fileDialog.file != null) {
                    File(fileDialog.directory, fileDialog.file)
                } else null

                continuation.resume(selectedFile)
            } catch (_: Exception) {
                continuation.resume(null)
            }
        }
    }

    return withContext(Dispatchers.IO) {
        if (file != null) {
            try {
                val mimeType = getMimeTypeFromFileName(file.name)
                val bytes = Files.readAllBytes(file.toPath())
                PickedImageData(
                    bytes = bytes,
                    mimeType = mimeType
                )
            } catch (_: Exception) {
                coroutineContext.ensureActive()
                null
            }
        } else {
            null
        }
    }
}
