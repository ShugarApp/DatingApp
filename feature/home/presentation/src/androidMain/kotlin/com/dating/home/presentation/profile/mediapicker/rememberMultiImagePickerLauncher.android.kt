package com.dating.home.presentation.profile.mediapicker

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

@Composable
actual fun rememberMultiImagePickerLauncher(
    maxSelection: Int,
    onResult: (List<PickedImageData>) -> Unit
): ImagePickerLauncher {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val multiPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = maxSelection)
    ) { uris ->
        scope.launch {
            val parser = ContentUriParser(context)
            val results = uris.mapNotNull { uri ->
                val mimeType = parser.getMimeType(uri)
                val bytes = parser.readUri(uri) ?: return@mapNotNull null
                PickedImageData(bytes = bytes, mimeType = mimeType)
            }
            if (results.isNotEmpty()) {
                onResult(results)
            }
        }
    }

    return remember {
        ImagePickerLauncher(
            onLaunch = {
                multiPickerLauncher.launch(
                    PickVisualMediaRequest(
                        mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            }
        )
    }
}
