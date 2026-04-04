package com.dating.home.domain.emergency

data class EmergencySettings(
    val isEnabled: Boolean = false,
    val autoCall911: Boolean = false,
    val onboardingSeen: Boolean = false
)
