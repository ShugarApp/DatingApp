package com.dating.aura.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class FirebaseAnalyticsService(context: Context) : AnalyticsService {

    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context)

    override fun trackSwipe(direction: SwipeDirection, targetUserId: String) {
        val bundle = Bundle().apply {
            putString("direction", direction.name.lowercase())
            putString("target_user_id", targetUserId)
        }
        firebaseAnalytics.logEvent("swipe", bundle)
    }

    override fun trackMatch(matchId: String) {
        val bundle = Bundle().apply {
            putString("match_id", matchId)
        }
        firebaseAnalytics.logEvent("match", bundle)
    }

    override fun trackMessageSent(chatId: String) {
        val bundle = Bundle().apply {
            putString("chat_id", chatId)
        }
        firebaseAnalytics.logEvent("message_sent", bundle)
    }

    override fun trackScreenView(screenName: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenName)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    override fun trackLogin(method: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.METHOD, method)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
    }

    override fun trackSignUp(method: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.METHOD, method)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle)
    }
}
