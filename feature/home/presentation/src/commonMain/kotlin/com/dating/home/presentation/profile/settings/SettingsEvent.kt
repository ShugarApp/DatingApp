package com.dating.home.presentation.profile.settings

sealed interface SettingsEvent {
    data object OnLogoutSuccess : SettingsEvent
    data object OnDeleteAccountSuccess : SettingsEvent
    data object OnPauseAccountToggled : SettingsEvent
    data object OnIncognitoToggled : SettingsEvent
}
