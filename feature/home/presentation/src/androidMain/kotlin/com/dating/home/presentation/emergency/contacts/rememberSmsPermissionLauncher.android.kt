package com.dating.home.presentation.emergency.contacts

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberSmsPermissionLauncher(onResult: (Boolean) -> Unit): SmsPermissionLauncher {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> onResult(granted) }
    return remember { SmsPermissionLauncher { launcher.launch(Manifest.permission.SEND_SMS) } }
}
