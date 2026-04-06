package com.dating.home.presentation.emergency.contacts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

// iOS uses the sms: URL scheme — no runtime permission needed
@Composable
actual fun rememberSmsPermissionLauncher(onResult: (Boolean) -> Unit): SmsPermissionLauncher =
    remember { SmsPermissionLauncher { onResult(true) } }
