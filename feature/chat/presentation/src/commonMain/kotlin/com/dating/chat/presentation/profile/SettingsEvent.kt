package com.dating.chat.presentation.profile

sealed interface SettingsEvent {
    data object OnLogoutSuccess: SettingsEvent
}
