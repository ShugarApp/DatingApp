package com.dating.home.presentation.profile.settings

sealed interface SettingsAction {
    data object OnLogoutClick: SettingsAction
    data object OnConfirmLogoutClick: SettingsAction
    data object OnDismissLogoutConfirmationDialogClick: SettingsAction
}
