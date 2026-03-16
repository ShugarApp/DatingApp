package com.dating.home.presentation.profile.profile

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dating.core.domain.location.LocationProvider
import com.dating.core.domain.auth.SessionStorage
import com.dating.core.domain.util.onFailure
import com.dating.core.domain.util.onSuccess
import com.dating.home.domain.participant.ChatParticipantRepository
import com.dating.home.domain.user.UserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import kotlin.math.abs

class ProfileViewModel(
    private val chatParticipantRepository: ChatParticipantRepository,
    private val sessionStorage: SessionStorage,
    private val userService: UserService,
    private val locationProvider: LocationProvider
) : ViewModel() {

    companion object {
        private var hasProfileSynced = false
        private var hasLocationSentThisSession = false
        private var lastSentLatitude: Double? = null
        private var lastSentLongitude: Double? = null
    }

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(ProfileState())
    val state = combine(
        _state,
        sessionStorage.observeAuthInfo()
    ) { currentState, authInfo ->
        if (authInfo != null) {
            currentState.copy(
                userId = authInfo.user.id,
                username = authInfo.user.username,
                userInitials = authInfo.user.username.take(2),
                emailTextState = TextFieldState(initialText = authInfo.user.email),
                photos = authInfo.user.photos,
                city = authInfo.user.city,
                country = authInfo.user.country,
                bio = authInfo.user.bio,
                jobTitle = authInfo.user.jobTitle,
                company = authInfo.user.company,
                education = authInfo.user.education,
                height = authInfo.user.height,
                zodiac = authInfo.user.zodiac,
                smoking = authInfo.user.smoking,
                drinking = authInfo.user.drinking,
                interests = authInfo.user.interests
            )
        } else currentState
    }
        .onStart {
            if (!hasLoadedInitialData) {
                if (!hasProfileSynced) {
                    syncProfileOnce()
                }
                fetchLocalParticipantDetails()
                updateLocationOnAppOpen()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = ProfileState()
        )

    fun onAction(action: ProfileAction) {
        when (action) {
            is ProfileAction.OnUpdateLocation -> updateLocation()
            is ProfileAction.OnAvatarClick -> loadProfilePreview()
            is ProfileAction.OnDismissPreview -> _state.update { it.copy(showPreview = false) }
        }
    }

    private fun loadProfilePreview() {
        viewModelScope.launch {
            _state.update { it.copy(showPreview = true, isLoadingPreview = true) }
            userService.getMyProfile()
                .onSuccess { user ->
                    // Sync session so the combine doesn't overwrite with stale data
                    val authInfo = sessionStorage.observeAuthInfo().firstOrNull()
                    if (authInfo != null) {
                        sessionStorage.set(authInfo.copy(user = user))
                    }
                    _state.update { it.copy(isLoadingPreview = false) }
                }
                .onFailure {
                    _state.update { it.copy(isLoadingPreview = false) }
                }
        }
    }

    private fun syncProfileOnce() {
        viewModelScope.launch {
            userService.getMyProfile()
                .onSuccess { user ->
                    val authInfo = sessionStorage.observeAuthInfo().firstOrNull()
                    if (authInfo != null) {
                        sessionStorage.set(authInfo.copy(user = user))
                    }
                    hasProfileSynced = true
                }
        }
    }

    private fun fetchLocalParticipantDetails() {
        viewModelScope.launch {
            chatParticipantRepository.fetchLocalParticipant()
        }
    }

    private fun hasLocationChangedSignificantly(lat: Double, lng: Double): Boolean {
        val prevLat = lastSentLatitude ?: return true
        val prevLng = lastSentLongitude ?: return true
        // ~0.0005 degrees ≈ 50 meters
        return abs(lat - prevLat) > 0.0005 || abs(lng - prevLng) > 0.0005
    }

    private fun updateLocationOnAppOpen() {
        if (hasLocationSentThisSession) return
        viewModelScope.launch {
            val location = locationProvider.getLastKnownLocation() ?: return@launch
            if (!hasLocationChangedSignificantly(location.latitude, location.longitude)) {
                hasLocationSentThisSession = true
                return@launch
            }
            userService.updateLocation(location.latitude, location.longitude)
                .onSuccess { updatedUser ->
                    lastSentLatitude = location.latitude
                    lastSentLongitude = location.longitude
                    hasLocationSentThisSession = true
                    persistLocationToSession(updatedUser.city, updatedUser.country)
                }
        }
    }

    private fun updateLocation() {
        viewModelScope.launch {
            _state.update { it.copy(isUpdatingLocation = true, locationError = null) }
            yield() // allow Compose to recompose and show loading state
            val location = locationProvider.getLastKnownLocation()
            if (location == null) {
                _state.update { it.copy(isUpdatingLocation = false, locationError = "Could not get location") }
                return@launch
            }
            userService.updateLocation(location.latitude, location.longitude)
                .onSuccess { updatedUser ->
                    _state.update {
                        it.copy(
                            isUpdatingLocation = false,
                            city = updatedUser.city,
                            country = updatedUser.country
                        )
                    }
                    persistLocationToSession(updatedUser.city, updatedUser.country)
                }
                .onFailure {
                    _state.update { it.copy(isUpdatingLocation = false, locationError = "Could not update location") }
                }
        }
    }

    private suspend fun persistLocationToSession(city: String?, country: String?) {
        val authInfo = sessionStorage.observeAuthInfo().firstOrNull() ?: return
        sessionStorage.set(
            authInfo.copy(user = authInfo.user.copy(city = city, country = country))
        )
    }
}
