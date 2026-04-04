package com.dating.home.data.emergency

actual class SmsDispatcher actual constructor() {
    actual suspend fun send(phoneNumber: String, message: String): Boolean {
        // SMS not supported on desktop
        return false
    }
}
