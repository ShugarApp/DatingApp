package com.dating.chat.data.notification

import com.dating.chat.domain.notification.PushNotificationService
import kotlinx.coroutines.flow.Flow

expect class FirebasePushNotificationService: PushNotificationService {
    override fun observeDeviceToken(): Flow<String?>
}