package com.dating.aura

import com.dating.home.data.inactivity.InactivityNotificationStrings
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter

object IosInactivityBridge {

    private const val NOTIFICATION_IDENTIFIER = "aura_inactivity_notification"
    private const val INACTIVITY_SECONDS = 5.0 * 24 * 60 * 60 // 5 días en segundos

    fun onAppForegrounded() {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        center.removePendingNotificationRequestsWithIdentifiers(listOf(NOTIFICATION_IDENTIFIER))

        val content = UNMutableNotificationContent()
        content.setTitle(InactivityNotificationStrings.TITLE)
        content.setBody(InactivityNotificationStrings.BODY)
        content.setSound(UNNotificationSound.defaultSound)

        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
            timeInterval = INACTIVITY_SECONDS,
            repeats = false
        )

        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = NOTIFICATION_IDENTIFIER,
            content = content,
            trigger = trigger
        )

        center.addNotificationRequest(request) { error ->
            if (error != null) {
                println("IosInactivityBridge: error al programar notificación: $error")
            }
        }
    }
}
