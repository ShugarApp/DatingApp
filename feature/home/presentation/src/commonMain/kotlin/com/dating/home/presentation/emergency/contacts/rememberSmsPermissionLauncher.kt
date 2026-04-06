package com.dating.home.presentation.emergency.contacts

import androidx.compose.runtime.Composable

class SmsPermissionLauncher(private val onLaunch: () -> Unit) {
    fun launch() = onLaunch()
}

@Composable
expect fun rememberSmsPermissionLauncher(onResult: (granted: Boolean) -> Unit): SmsPermissionLauncher
