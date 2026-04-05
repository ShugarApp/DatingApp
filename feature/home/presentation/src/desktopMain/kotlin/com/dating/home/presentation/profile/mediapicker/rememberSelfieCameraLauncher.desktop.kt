package com.dating.home.presentation.profile.mediapicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

// Desktop fallback: use file picker since no camera API is available in Compose Desktop.
@Composable
actual fun rememberSelfieCameraLauncher(
    onResult: (PickedImageData?) -> Unit
): SelfieCameraLauncher {
    val scope = rememberCoroutineScope()
    return remember {
        SelfieCameraLauncher {
            scope.launch {
                pickImage("Select selfie image")?.let { onResult(it) } ?: onResult(null)
            }
        }
    }
}
