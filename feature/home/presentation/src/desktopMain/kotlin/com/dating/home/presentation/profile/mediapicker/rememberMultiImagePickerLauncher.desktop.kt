package com.dating.home.presentation.profile.mediapicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import aura.feature.home.presentation.generated.resources.Res
import aura.feature.home.presentation.generated.resources.select_a_profile_picture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.stringResource
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.io.FilenameFilter
import java.nio.file.Files
import javax.swing.SwingUtilities
import kotlin.coroutines.resume

@Composable
actual fun rememberMultiImagePickerLauncher(
    maxSelection: Int,
    onResult: (List<PickedImageData>) -> Unit
): ImagePickerLauncher {
    val scope = rememberCoroutineScope()
    val dialogTitle = stringResource(Res.string.select_a_profile_picture)
    return remember {
        ImagePickerLauncher(
            onLaunch = {
                scope.launch {
                    val images = pickMultipleImages(dialogTitle)
                    if (images.isNotEmpty()) {
                        onResult(images.take(maxSelection))
                    }
                }
            }
        )
    }
}

private suspend fun pickMultipleImages(fileDialogTitle: String): List<PickedImageData> {
    val files = suspendCancellableCoroutine<List<File>> { continuation ->
        var fileDialog: FileDialog? = null

        continuation.invokeOnCancellation {
            SwingUtilities.invokeLater {
                fileDialog?.dispose()
            }
        }

        SwingUtilities.invokeLater {
            try {
                fileDialog = FileDialog(Frame(), fileDialogTitle, FileDialog.LOAD)
                fileDialog!!.isMultipleMode = true
                fileDialog!!.filenameFilter = FilenameFilter { _, name ->
                    allowedImageExtensions.any { name.endsWith(it) }
                }
                fileDialog!!.isVisible = true

                val selectedFiles = fileDialog!!.files?.toList() ?: emptyList()
                continuation.resume(selectedFiles)
            } catch (_: Exception) {
                continuation.resume(emptyList())
            }
        }
    }

    return withContext(Dispatchers.IO) {
        files.mapNotNull { file ->
            try {
                val mimeType = getMimeTypeFromFileName(file.name)
                val bytes = Files.readAllBytes(file.toPath())
                PickedImageData(bytes = bytes, mimeType = mimeType)
            } catch (_: Exception) {
                coroutineContext.ensureActive()
                null
            }
        }
    }
}
