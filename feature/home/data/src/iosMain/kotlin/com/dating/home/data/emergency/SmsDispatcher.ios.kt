package com.dating.home.data.emergency

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual class SmsDispatcher actual constructor() {
    actual suspend fun send(phoneNumber: String, message: String): Boolean {
        return try {
            val encodedBody = message.replace(" ", "%20").replace("\n", "%0A")
            val urlString = "sms:$phoneNumber&body=$encodedBody"
            val url = NSURL.URLWithString(urlString) ?: return false
            UIApplication.sharedApplication.openURL(url)
            true
        } catch (e: Exception) {
            false
        }
    }
}
