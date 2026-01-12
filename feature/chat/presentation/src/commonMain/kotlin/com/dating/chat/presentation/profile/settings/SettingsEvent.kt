package com.dating.chat.presentation.profile.settings

sealed interface SettingsEvent {
    data object OnLogoutSuccess: SettingsEvent
}
