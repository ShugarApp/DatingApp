package com.dating.home.presentation.profile.edit_profile

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aura.feature.home.presentation.generated.resources.Res
import aura.feature.home.presentation.generated.resources.error_invalid_file_type
import com.dating.core.domain.auth.SessionStorage
import com.dating.core.domain.util.onFailure
import com.dating.core.domain.util.onSuccess
import com.dating.core.presentation.util.UiText
import com.dating.core.presentation.util.toUiText
import com.dating.home.domain.participant.ChatParticipantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val chatParticipantRepository: ChatParticipantRepository,
    private val sessionStorage: SessionStorage,
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(EditProfileState())
    val state = combine(
        _state,
        sessionStorage.observeAuthInfo()
    ) { currentState, authInfo ->
        if(authInfo != null && !hasLoadedInitialData) {
             currentState.copy(
                profilePictureUrl = authInfo.user.profilePictureUrl,
                 // Mock Pre-fill for demo purposes
                bioTextState = TextFieldState("Hello! I'm a UX Designer who loves hiking and coffee."),
                jobTitleTextState = TextFieldState("UX Designer"),
                companyTextState = TextFieldState("Spotify"),
                educationTextState = TextFieldState("University of Arts"),
                locationTextState = TextFieldState("New York, USA"),
                selectedInterests = listOf("Design", "Music", "Coffee", "Hiking"),
                photos = listOf(authInfo.user.profilePictureUrl, null, null, null, null, null)
            )
        } else currentState
    }
        .onStart {
            if (!hasLoadedInitialData) {
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = EditProfileState()
        )

    fun onAction(action: EditProfileAction) {
        when (action) {
            is EditProfileAction.OnPictureSelected -> uploadProfilePicture(action.bytes, action.mimeType)
            is EditProfileAction.OnDeletePictureClick -> showDeleteConfirmation()
            is EditProfileAction.OnConfirmDeleteClick -> deleteProfilePicture()
            is EditProfileAction.OnDismissDeleteConfirmationDialogClick -> dismissDeleteConfirmation()
            is EditProfileAction.OnInterestSelected -> toggleInterest(action.interest)
            is EditProfileAction.OnInterestRemoved -> toggleInterest(action.interest)
        }
    }

    private fun toggleInterest(interest: String) {
        _state.update { 
            val currentInterests = it.selectedInterests.toMutableList()
            if (currentInterests.contains(interest)) {
                currentInterests.remove(interest)
            } else {
                currentInterests.add(interest)
            }
            it.copy(selectedInterests = currentInterests)
        }
    }

    private fun deleteProfilePicture() {
        if(state.value.isDeletingImage && state.value.profilePictureUrl == null) {
            return
        }

        _state.update { it.copy(
            isDeletingImage = true,
            imageError = null,
            showDeleteConfirmationDialog = false
        ) }

        viewModelScope.launch {
            chatParticipantRepository
                .deleteProfilePicture()
                .onSuccess {
                    _state.update { it.copy(
                        isDeletingImage = false
                    ) }
                }
                .onFailure { error ->
                    _state.update { it.copy(
                        imageError = error.toUiText(),
                        isDeletingImage = false
                    ) }
                }
        }
    }

    private fun dismissDeleteConfirmation() {
        _state.update { it.copy(
            showDeleteConfirmationDialog = false
        ) }
    }

    private fun showDeleteConfirmation() {
        _state.update { it.copy(
            showDeleteConfirmationDialog = true
        )}
    }

    private fun uploadProfilePicture(bytes: ByteArray, mimeType: String?) {
        if(state.value.isUploadingImage) {
            return
        }

        if(mimeType == null) {
            _state.update { it.copy(
                imageError = UiText.Resource(Res.string.error_invalid_file_type)
            ) }
            return
        }

        _state.update { it.copy(
            isUploadingImage = true,
            imageError = null
        ) }

        viewModelScope.launch {
            chatParticipantRepository
                .uploadProfilePicture(
                    imageBytes = bytes,
                    mimeType = mimeType
                )
                .onSuccess {
                    _state.update { it.copy(
                        isUploadingImage = false,
                    ) }
                }
                .onFailure { error ->
                    _state.update { it.copy(
                        imageError = error.toUiText(),
                        isUploadingImage = false
                    ) }
                }
        }
    }
}
