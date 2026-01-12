package com.dating.home.data.notification

import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.dating.home.domain.notification.PushNotificationService
import com.dating.core.domain.logging.AppLogger
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.coroutineContext

actual class FirebasePushNotificationService(
    private val logger: AppLogger
) : PushNotificationService {

    actual override fun observeDeviceToken(): Flow<String?> = flow {
        try {
            val fcmToken = Firebase.messaging.token.await()
            logger.info("Initial FCM token received: $fcmToken")
            emit(fcmToken)
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            logger.error("Failed to get FCM token", e)
            emit(null)
        }
    }
}