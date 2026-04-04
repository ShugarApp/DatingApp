package com.dating.core.domain.preferences

interface OnboardingPreferences {
    suspend fun hasSeenFeaturesOnboarding(): Boolean
    suspend fun markFeaturesOnboardingSeen()
}
