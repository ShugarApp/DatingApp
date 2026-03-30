package com.dating.home.presentation.profile.settings

import com.dating.core.domain.discovery.Gender
import com.dating.core.domain.preferences.ThemePreference

sealed interface SettingsAction {
    data object OnLogoutClick : SettingsAction
    data object OnConfirmLogoutClick : SettingsAction
    data object OnDismissLogoutConfirmationDialogClick : SettingsAction

    data object OnConfirmPauseAccountClick : SettingsAction
    data object OnToggleIncognitoClick : SettingsAction
    data object OnConfirmDeleteAccountClick : SettingsAction

    // Discovery
    data object OnLocationClick : SettingsAction
    data object OnMaxDistanceClick : SettingsAction
    data object OnShowMeClick : SettingsAction
    data object OnAgeRangeClick : SettingsAction
    data object OnDismissDiscoveryDialog : SettingsAction
    data class OnMaxDistanceChanged(val distance: Double?) : SettingsAction
    data class OnShowMeChanged(val gender: Gender) : SettingsAction
    data class OnAgeRangeChanged(val minAge: Int, val maxAge: Int) : SettingsAction

    // Theme
    data object OnThemeClick : SettingsAction
    data class OnThemeChanged(val theme: ThemePreference) : SettingsAction

    data object OnDismissError : SettingsAction
}
