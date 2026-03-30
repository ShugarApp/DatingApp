package com.dating.home.presentation.chat.mediapicker

import androidx.compose.runtime.Composable
import com.dating.home.presentation.profile.mediapicker.PickedImageData

enum class MediaFilter {
    IMAGES_AND_GIFS,
    AUDIO
}

@Composable
expect fun rememberMediaPickerLauncher(
    mediaFilter: MediaFilter,
    onResult: (PickedImageData) -> Unit
): MediaPickerLauncher

class MediaPickerLauncher(
    private val onLaunch: () -> Unit
) {
    fun launch() {
        onLaunch()
    }
}
