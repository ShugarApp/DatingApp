package com.dating.chat.presentation.profile

sealed interface SettingsAction {
    data object OnLogoutClick: SettingsAction
    data object OnConfirmLogoutClick: SettingsAction
    data object OnDismissLogoutConfirmationDialogClick: SettingsAction
}
