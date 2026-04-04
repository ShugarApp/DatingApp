package com.dating.home.presentation.profile.mediapicker

import androidx.compose.runtime.Composable

@Composable
expect fun rememberMultiImagePickerLauncher(
    maxSelection: Int,
    onResult: (List<PickedImageData>) -> Unit
): ImagePickerLauncher
