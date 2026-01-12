package com.dating.chat.presentation.profile.settings

data class SettingsState(
    val username: String = "",
    val showLogoutConfirmationDialog: Boolean = false
)
