package com.dating.home.presentation.profile.settings

import com.dating.core.domain.discovery.Gender
import com.dating.core.domain.preferences.ThemePreference
import com.dating.core.presentation.util.UiText

data class SettingsState(
    val username: String = "",
    val isAccountPaused: Boolean = false,
    val isIncognito: Boolean = false,
    val isLoading: Boolean = false,
    val showLogoutConfirmationDialog: Boolean = false,
    val maxDistance: Double? = null,
    val showMe: Gender = Gender.WOMEN,
    val minAge: Int = 18,
    val maxAge: Int = 50,
    val isUpdatingLocation: Boolean = false,
    val locationUpdateSuccess: Boolean? = null,
    val showDistanceDialog: Boolean = false,
    val showGenderDialog: Boolean = false,
    val showAgeRangeDialog: Boolean = false,
    val themePreference: ThemePreference = ThemePreference.SYSTEM,
    val showThemeDialog: Boolean = false,
    val errorMessage: UiText? = null,
    val showDeleteSurveyDialog: Boolean = false,
    val selectedDeleteReason: DeleteAccountReason? = null,
    val isEmergencyEnabled: Boolean = false
)
