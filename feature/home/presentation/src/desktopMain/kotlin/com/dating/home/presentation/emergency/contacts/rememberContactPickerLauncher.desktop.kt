package com.dating.home.presentation.emergency.contacts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberContactPickerLauncher(
    onResult: (PickedContactData?) -> Unit
): ContactPickerLauncher = remember { ContactPickerLauncher { onResult(null) } }
