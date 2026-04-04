package com.dating.home.presentation.photo_onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dating.core.domain.auth.SessionStorage
import com.dating.core.domain.auth.UserStatus
import com.dating.core.domain.util.onFailure
import com.dating.core.domain.util.onSuccess
import com.dating.core.presentation.util.UiText
import com.dating.core.presentation.util.toUiText
import com.dating.home.domain.user.UserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import aura.feature.home.presentation.generated.resources.Res
import aura.feature.home.presentation.generated.resources.error_invalid_file_type

class PhotoOnboardingViewModel(
    private val sessionStorage: SessionStorage,
    private val userService: UserService
) : ViewModel() {

    private val _state = MutableStateFlow(PhotoOnboardingState())
    val state = _state
        .onStart { loadPhotos() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = PhotoOnboardingState()
        )

    private fun loadPhotos() {
        viewModelScope.launch {
            // Load cached photos first for instant display
            val authInfo = sessionStorage.observeAuthInfo().firstOrNull()
            val cachedPhotos = authInfo?.user?.photos ?: emptyList()
            _state.update {
                it.copy(photos = List(4) { i -> cachedPhotos.getOrNull(i) })
            }
            // Sync with server to get the latest photos and status
            userService.getMyProfile()
                .onSuccess { user ->
                    _state.update {
                        it.copy(photos = List(4) { i -> user.photos.getOrNull(i) })
                    }
                    if (authInfo != null) {
                        sessionStorage.set(authInfo.copy(user = user))
                    }
                }
        }
    }

    fun onPhotoSelected(bytes: ByteArray, mimeType: String?, slotIndex: Int) {
        if (slotIndex in _state.value.uploadingSlots) return
        if (mimeType == null) {
            _state.update { it.copy(imageError = UiText.Resource(Res.string.error_invalid_file_type)) }
            return
        }
        _state.update {
            it.copy(
                uploadingSlots = it.uploadingSlots + slotIndex,
                imageError = null
            )
        }
        viewModelScope.launch {
            userService.uploadPhoto(imageBytes = bytes, mimeType = mimeType, index = slotIndex)
                .onSuccess { publicUrl ->
                    val updatedPhotos = _state.value.photos.toMutableList()
                    updatedPhotos[slotIndex] = publicUrl
                    _state.update {
                        it.copy(
                            uploadingSlots = it.uploadingSlots - slotIndex,
                            photos = updatedPhotos
                        )
                    }
                    updateSessionPhotos(updatedPhotos.filterNotNull())
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            uploadingSlots = it.uploadingSlots - slotIndex,
                            imageError = error.toUiText()
                        )
                    }
                }
        }
    }

    fun onPhotoUploaded(slotIndex: Int, publicUrl: String) {
        val updatedPhotos = _state.value.photos.toMutableList()
        updatedPhotos[slotIndex] = publicUrl
        _state.update {
            it.copy(
                uploadingSlots = it.uploadingSlots - slotIndex,
                photos = updatedPhotos
            )
        }
        updateSessionPhotos(updatedPhotos.filterNotNull())
    }

    fun onPhotoUploadFailed(slotIndex: Int) {
        _state.update {
            it.copy(
                uploadingSlots = it.uploadingSlots - slotIndex,
                imageError = UiText.DynamicString("Error uploading photo")
            )
        }
    }

    fun onDismissError() {
        _state.update { it.copy(imageError = null) }
    }

    fun onComplete() {
        _state.update { it.copy(isCompleting = true) }
        viewModelScope.launch {
            userService.getMyProfile()
                .onSuccess { user ->
                    sessionStorage.observeAuthInfo().firstOrNull()?.let { info ->
                        sessionStorage.set(info.copy(user = user))
                    }
                    // If backend hasn't changed status yet, reset loading
                    if (user.status == UserStatus.PENDING) {
                        _state.update { it.copy(isCompleting = false) }
                    }
                }
                .onFailure {
                    _state.update { it.copy(isCompleting = false) }
                }
        }
    }

    private fun updateSessionPhotos(photos: List<String>) {
        viewModelScope.launch {
            sessionStorage.observeAuthInfo().firstOrNull()?.let { info ->
                sessionStorage.set(info.copy(user = info.user.copy(photos = photos)))
            }
        }
    }
}
