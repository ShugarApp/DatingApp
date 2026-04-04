package com.dating.core.domain.preferences

interface OnboardingPreferences {
    suspend fun hasSeenFeaturesOnboarding(): Boolean
    suspend fun markFeaturesOnboardingSeen()
    suspend fun hasSeenProfileSetup(): Boolean
    suspend fun markProfileSetupSeen()
}
