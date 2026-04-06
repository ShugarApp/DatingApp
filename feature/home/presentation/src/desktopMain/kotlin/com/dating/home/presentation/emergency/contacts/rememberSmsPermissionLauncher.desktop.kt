package com.dating.home.presentation.emergency.contacts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberSmsPermissionLauncher(onResult: (Boolean) -> Unit): SmsPermissionLauncher =
    remember { SmsPermissionLauncher { onResult(false) } }
