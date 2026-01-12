package com.dating.home.presentation.profile.settings

sealed interface SettingsEvent {
    data object OnLogoutSuccess: SettingsEvent
}
