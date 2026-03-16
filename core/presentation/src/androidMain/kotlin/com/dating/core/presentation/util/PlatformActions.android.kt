package com.dating.core.presentation.util

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberOpenUrl(): (String) -> Unit {
    val context = LocalContext.current
    return remember {
        { url: String ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        }
    }
}

@Composable
actual fun rememberOpenNotificationSettings(): () -> Unit {
    val context = LocalContext.current
    return remember {
        {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            }
            context.startActivity(intent)
        }
    }
}
