package com.dating.core.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString

@Composable
actual fun rememberOpenUrl(): (String) -> Unit {
    return remember {
        { url: String ->
            val nsUrl = NSURL.URLWithString(url) ?: return@remember
            UIApplication.sharedApplication.openURL(nsUrl)
        }
    }
}

@Composable
actual fun rememberOpenNotificationSettings(): () -> Unit {
    return remember {
        {
            val url = NSURL.URLWithString(UIApplicationOpenSettingsURLString) ?: return@remember
            UIApplication.sharedApplication.openURL(url)
        }
    }
}
