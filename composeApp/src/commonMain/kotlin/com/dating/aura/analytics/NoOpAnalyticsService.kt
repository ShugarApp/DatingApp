package com.dating.aura.analytics

class NoOpAnalyticsService : AnalyticsService {
    override fun trackSwipe(direction: SwipeDirection, targetUserId: String) = Unit
    override fun trackMatch(matchId: String) = Unit
    override fun trackMessageSent(chatId: String) = Unit
    override fun trackScreenView(screenName: String) = Unit
    override fun trackLogin(method: String) = Unit
    override fun trackSignUp(method: String) = Unit
}
