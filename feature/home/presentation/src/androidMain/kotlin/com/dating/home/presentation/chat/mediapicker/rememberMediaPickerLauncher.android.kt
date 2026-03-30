package com.dating.home.presentation.chat.mediapicker

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.dating.home.presentation.profile.mediapicker.ContentUriParser
import com.dating.home.presentation.profile.mediapicker.PickedImageData
import kotlinx.coroutines.launch

@Composable
actual fun rememberMediaPickerLauncher(
    mediaFilter: MediaFilter,
    onResult: (PickedImageData) -> Unit
): MediaPickerLauncher {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val contentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val parser = ContentUriParser(context)
            val mimeType = parser.getMimeType(uri)

            scope.launch {
                val data = PickedImageData(
                    bytes = parser.readUri(uri) ?: return@launch,
                    mimeType = mimeType
                )
                onResult(data)
            }
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val parser = ContentUriParser(context)
            val mimeType = parser.getMimeType(uri)

            scope.launch {
                val data = PickedImageData(
                    bytes = parser.readUri(uri) ?: return@launch,
                    mimeType = mimeType
                )
                onResult(data)
            }
        }
    }

    return remember(mediaFilter) {
        MediaPickerLauncher(
            onLaunch = {
                when (mediaFilter) {
                    MediaFilter.IMAGES_AND_GIFS -> {
                        try {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(
                                    mediaType = ActivityResultContracts.PickVisualMedia.ImageAndVideo
                                )
                            )
                        } catch (e: Exception) {
                            contentLauncher.launch("image/*")
                        }
                    }
                    MediaFilter.AUDIO -> {
                        contentLauncher.launch("audio/*")
                    }
                }
            }
        )
    }
}
