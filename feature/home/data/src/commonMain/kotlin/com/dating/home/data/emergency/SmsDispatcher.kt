package com.dating.home.data.emergency

expect class SmsDispatcher() {
    suspend fun send(phoneNumber: String, message: String): Boolean
}
