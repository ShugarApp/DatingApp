package com.dating.core.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.awt.Desktop
import java.net.URI

@Composable
actual fun rememberOpenUrl(): (String) -> Unit {
    return remember {
        { url: String ->
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(URI(url))
            }
        }
    }
}

@Composable
actual fun rememberOpenNotificationSettings(): () -> Unit {
    return remember {
        { /* No-op on desktop */ }
    }
}
