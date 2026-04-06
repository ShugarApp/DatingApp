package com.dating.home.domain.emergency

interface SmsDispatcher {
    suspend fun send(phoneNumber: String, message: String): Boolean
}
