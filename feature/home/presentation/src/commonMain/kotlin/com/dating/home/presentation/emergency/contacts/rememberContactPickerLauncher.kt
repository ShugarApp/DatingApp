package com.dating.home.presentation.emergency.contacts

import androidx.compose.runtime.Composable

data class PickedContactData(
    val name: String,
    val phoneNumber: String
)

class ContactPickerLauncher(private val onLaunch: () -> Unit) {
    fun launch() = onLaunch()
}

@Composable
expect fun rememberContactPickerLauncher(
    onResult: (PickedContactData?) -> Unit
): ContactPickerLauncher
