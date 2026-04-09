package com.dating.home.presentation.profile.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dating.core.domain.auth.AuthService
import com.dating.core.domain.auth.SessionStorage
import com.dating.core.domain.discovery.DiscoveryPreferencesStorage
import com.dating.core.domain.location.LocationProvider
import com.dating.core.domain.preferences.ThemePreferences
import com.dating.core.domain.util.Result
import com.dating.core.domain.util.onFailure
import com.dating.core.domain.util.onSuccess
import com.dating.core.presentation.util.toUiText
import com.dating.home.domain.emergency.EmergencySettingsStorage
import com.dating.home.domain.user.UserService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val authService: AuthService,
    private val sessionStorage: SessionStorage,
    private val userService: UserService,
    private val discoveryPreferences: DiscoveryPreferencesStorage,
    private val locationProvider: LocationProvider,
    private val themePreferences: ThemePreferences,
    private val emergencySettingsStorage: EmergencySettingsStorage
) : ViewModel() {

    private val _events = Channel<SettingsEvent>()
    val events = _events.receiveAsFlow()

    private val _state = MutableStateFlow(SettingsState())
    val state = combine(
        _state,
        sessionStorage.observeAuthInfo(),
        discoveryPreferences.observe(),
        themePreferences.observeThemePreference(),
        emergencySettingsStorage.observe()
    ) { currentState, authInfo, discovery, theme, emergency ->
        currentState.copy(
            username = authInfo?.user?.username ?: currentState.username,
            isAccountPaused = authInfo?.user?.isPaused ?: currentState.isAccountPaused,
            isIncognito = authInfo?.user?.isIncognito ?: currentState.isIncognito,
            maxDistance = discovery.maxDistance,
            showMe = discovery.showMe,
            minAge = discovery.minAge,
            maxAge = discovery.maxAge,
            verifiedProfilesOnly = discovery.verifiedProfilesOnly,
            themePreference = theme,
            isEmergencyEnabled = emergency.isEnabled
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = SettingsState()
    )

    fun onAction(action: SettingsAction) {
        when (action) {
            SettingsAction.OnLogoutClick -> showLogoutConfirmation()
            SettingsAction.OnConfirmLogoutClick -> logout()
            SettingsAction.OnDismissLogoutConfirmationDialogClick -> dismissLogoutConfirmation()
            SettingsAction.OnConfirmPauseAccountClick -> togglePauseAccount()
            SettingsAction.OnToggleIncognitoClick -> toggleIncognitoMode()
            SettingsAction.OnDeleteAccountClick -> _state.update { it.copy(showDeleteSurveyDialog = true) }
            is SettingsAction.OnSurveyReasonSelected -> _state.update { it.copy(selectedDeleteReason = action.reason) }
            SettingsAction.OnSurveyConfirmClick -> {
                _state.update { it.copy(showDeleteSurveyDialog = false) }
                viewModelScope.launch { _events.send(SettingsEvent.OnSurveyCompleted) }
            }
            SettingsAction.OnDismissSurvey -> _state.update {
                it.copy(showDeleteSurveyDialog = false, selectedDeleteReason = null)
            }
            SettingsAction.OnConfirmDeleteAccountClick -> deleteAccount()

            SettingsAction.OnLocationClick -> updateLocation()
            SettingsAction.OnMaxDistanceClick -> _state.update { it.copy(showDistanceDialog = true) }
            SettingsAction.OnShowMeClick -> _state.update { it.copy(showGenderDialog = true) }
            SettingsAction.OnAgeRangeClick -> _state.update { it.copy(showAgeRangeDialog = true) }
            SettingsAction.OnDismissDiscoveryDialog -> _state.update {
                it.copy(showDistanceDialog = false, showGenderDialog = false, showAgeRangeDialog = false)
            }
            is SettingsAction.OnMaxDistanceChanged -> {
                viewModelScope.launch { discoveryPreferences.updateMaxDistance(action.distance) }
                _state.update { it.copy(showDistanceDialog = false) }
            }
            is SettingsAction.OnShowMeChanged -> {
                viewModelScope.launch { discoveryPreferences.updateShowMe(action.gender) }
                _state.update { it.copy(showGenderDialog = false) }
            }
            is SettingsAction.OnAgeRangeChanged -> {
                viewModelScope.launch { discoveryPreferences.updateAgeRange(action.minAge, action.maxAge) }
                _state.update { it.copy(showAgeRangeDialog = false) }
            }
            is SettingsAction.OnVerifiedProfilesOnlyChanged -> {
                viewModelScope.launch { discoveryPreferences.updateVerifiedProfilesOnly(action.enabled) }
            }

            SettingsAction.OnDismissError -> _state.update { it.copy(errorMessage = null) }
            SettingsAction.OnThemeClick -> _state.update { it.copy(showThemeDialog = true) }
            is SettingsAction.OnThemeChanged -> {
                viewModelScope.launch { themePreferences.updateThemePreference(action.theme) }
                _state.update { it.copy(showThemeDialog = false) }
            }
            is SettingsAction.OnEmergencyToggle -> {
                viewModelScope.launch { emergencySettingsStorage.setEnabled(action.enabled) }
            }
            SettingsAction.OnEmergencyContactsClick -> {
                viewModelScope.launch { _events.send(SettingsEvent.OnNavigateToEmergencyContacts) }
            }
            SettingsAction.OnEmergencyTutorialClick -> {
                viewModelScope.launch { _events.send(SettingsEvent.OnNavigateToEmergencyTutorial) }
            }
        }
    }

    private fun showLogoutConfirmation() {
        _state.update { it.copy(showLogoutConfirmationDialog = true) }
    }

    private fun dismissLogoutConfirmation() {
        _state.update { it.copy(showLogoutConfirmationDialog = false) }
    }

    private fun logout() {
        dismissLogoutConfirmation()
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val authInfo = sessionStorage.observeAuthInfo().first()
            val refreshToken = authInfo?.refreshToken ?: run {
                _state.update { it.copy(isLoading = false) }
                return@launch
            }
            authService.logout(refreshToken)
                .onSuccess {
                    sessionStorage.set(null)
                    _events.send(SettingsEvent.OnLogoutSuccess)
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, errorMessage = error.toUiText()) }
                }
        }
    }

    private fun updateLocation() {
        viewModelScope.launch {
            _state.update { it.copy(isUpdatingLocation = true, locationUpdateSuccess = null) }
            val location = locationProvider.getLastKnownLocation()
            if (location != null) {
                val result = userService.updateLocation(location.latitude, location.longitude)
                val success = result is Result.Success
                _state.update { it.copy(isUpdatingLocation = false, locationUpdateSuccess = success) }
                if (success) {
                    _events.send(SettingsEvent.OnLocationUpdated)
                }
            } else {
                _state.update { it.copy(isUpdatingLocation = false, locationUpdateSuccess = false) }
            }
        }
    }

    private fun togglePauseAccount() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val currentIsPaused = state.value.isAccountPaused
            userService.pauseAccount(!currentIsPaused)
                .onSuccess { user ->
                    _state.update { it.copy(isLoading = false) }
                    val authInfo = sessionStorage.observeAuthInfo().first()
                    if (authInfo != null) {
                        sessionStorage.set(authInfo.copy(user = user))
                    }
                    _events.send(SettingsEvent.OnPauseAccountToggled)
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, errorMessage = error.toUiText()) }
                }
        }
    }

    private fun toggleIncognitoMode() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val currentIsIncognito = state.value.isIncognito
            userService.toggleIncognitoMode(!currentIsIncognito)
                .onSuccess { user ->
                    _state.update { it.copy(isLoading = false) }
                    val authInfo = sessionStorage.observeAuthInfo().first()
                    if (authInfo != null) {
                        sessionStorage.set(authInfo.copy(user = user))
                    }
                    _events.send(SettingsEvent.OnIncognitoToggled)
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, errorMessage = error.toUiText()) }
                }
        }
    }

    private fun deleteAccount() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val reason = state.value.selectedDeleteReason?.name
            userService.deleteAccount(reason = reason)
                .onSuccess {
                    _state.update { it.copy(isLoading = false) }
                    sessionStorage.set(null)
                    _events.send(SettingsEvent.OnDeleteAccountSuccess)
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, errorMessage = error.toUiText()) }
                }
        }
    }
}
