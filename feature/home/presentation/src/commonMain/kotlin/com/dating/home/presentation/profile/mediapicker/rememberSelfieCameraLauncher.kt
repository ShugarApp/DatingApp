package com.dating.home.presentation.profile.mediapicker

import androidx.compose.runtime.Composable

class SelfieCameraLauncher(private val onLaunch: () -> Unit) {
    fun launch() = onLaunch()
}

@Composable
expect fun rememberSelfieCameraLauncher(
    onResult: (PickedImageData?) -> Unit
): SelfieCameraLauncher
