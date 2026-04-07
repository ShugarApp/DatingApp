package com.dating.aura.analytics

enum class SwipeDirection { LIKE, DISLIKE }

interface AnalyticsService {
    fun trackSwipe(direction: SwipeDirection, targetUserId: String)
    fun trackMatch(matchId: String)
    fun trackMessageSent(chatId: String)
    fun trackScreenView(screenName: String)
    fun trackLogin(method: String)
    fun trackSignUp(method: String)
}
