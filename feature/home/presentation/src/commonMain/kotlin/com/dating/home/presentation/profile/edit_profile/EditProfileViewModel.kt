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
import com.dating.home.domain.user.UserService
import kotlin.time.Clock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime

class EditProfileViewModel(
    private val sessionStorage: SessionStorage,
    private val userService: UserService
) : ViewModel() {

    private val _state = MutableStateFlow(EditProfileState())
    val state = _state
        .onStart { loadProfile() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = EditProfileState()
        )

    fun onAction(action: EditProfileAction) {
        when (action) {
            is EditProfileAction.OnPictureSelected -> uploadPhoto(action.bytes, action.mimeType, action.slotIndex)
            is EditProfileAction.OnDeletePhotoClick -> showDeleteConfirmation(action.slotIndex)
            is EditProfileAction.OnConfirmDeletePhoto -> deletePhoto()
            is EditProfileAction.OnDismissDeleteConfirmationDialogClick -> dismissDeleteConfirmation()
            is EditProfileAction.OnInterestToggled -> toggleInterest(action.interest)
            is EditProfileAction.OnSaveProfile -> saveProfile()
            is EditProfileAction.OnGenderChanged -> _state.update { it.copy(gender = action.gender) }
            is EditProfileAction.OnBirthDateChanged -> _state.update { it.copy(birthDate = action.birthDate, birthDateError = null) }
            is EditProfileAction.OnHeightChanged -> _state.update { it.copy(height = action.height, heightError = null) }
            is EditProfileAction.OnZodiacChanged -> _state.update { it.copy(zodiac = action.zodiac) }
            is EditProfileAction.OnSmokingChanged -> _state.update { it.copy(smoking = action.smoking) }
            is EditProfileAction.OnDrinkingChanged -> _state.update { it.copy(drinking = action.drinking) }
            is EditProfileAction.OnPhotosReordered -> reorderPhotos(action.newPhotos)
            is EditProfileAction.OnDismissSuccessMessage -> _state.update { it.copy(showSuccessMessage = false) }
            is EditProfileAction.OnPhotoUploaded -> onPhotoUploaded(action.slotIndex, action.publicUrl)
            is EditProfileAction.OnPhotoUploadFailed -> onPhotoUploadFailed(action.slotIndex)
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            userService.getMyProfile()
                .onSuccess { user ->
                    _state.update {
                        it.copy(
                            photos = List(6) { i -> user.photos.getOrNull(i) },
                            bioTextState = TextFieldState(initialText = user.bio ?: ""),
                            gender = user.gender,
                            birthDate = user.birthDate,
                            jobTitleTextState = TextFieldState(initialText = user.jobTitle ?: ""),
                            companyTextState = TextFieldState(initialText = user.company ?: ""),
                            educationTextState = TextFieldState(initialText = user.education ?: ""),
                            height = user.height,
                            zodiac = user.zodiac,
                            smoking = user.smoking,
                            drinking = user.drinking,
                            selectedInterests = user.interests
                        )
                    }
                }
        }
    }

    private fun saveProfile() {
        if (_state.value.isSavingProfile) return

        val bio = _state.value.bioTextState.text.toString().ifBlank { null }
        if (bio != null && bio.length > 500) {
            _state.update { it.copy(bioError = "La bio no puede superar los 500 caracteres") }
            return
        }

        val height = _state.value.height
        if (height != null && (height < 100 || height > 250)) {
            _state.update { it.copy(heightError = "La altura debe estar entre 100 y 250 cm") }
            return
        }

        val interests = _state.value.selectedInterests
        if (interests.size > 10) {
            _state.update { it.copy(interestsError = "Máximo 10 intereses") }
            return
        }

        val birthDate = _state.value.birthDate
        if (birthDate != null) {
            val today = Clock.System.now().toLocalDateTime(TimeZone.UTC).date
            val selected = try { LocalDate.parse(birthDate) } catch (_: Exception) { null }
            if (selected != null) {
                if (selected > today) {
                    _state.update { it.copy(birthDateError = "La fecha de nacimiento no puede ser en el futuro") }
                    return
                }
                val eighteenYearsAgo = try {
                    LocalDate(today.year - 18, today.month.number, today.day)
                } catch (_: Exception) {
                    LocalDate(today.year - 18, today.month.number, 28)
                }
                if (selected > eighteenYearsAgo) {
                    _state.update { it.copy(birthDateError = "Debes tener al menos 18 años") }
                    return
                }
            }
        }

        _state.update {
            it.copy(
                isSavingProfile = true,
                saveError = null,
                bioError = null,
                birthDateError = null,
                heightError = null,
                interestsError = null
            )
        }

        viewModelScope.launch {
            userService.updateProfile(
                bio = bio,
                gender = _state.value.gender,
                birthDate = _state.value.birthDate,
                jobTitle = _state.value.jobTitleTextState.text.toString().ifBlank { null },
                company = _state.value.companyTextState.text.toString().ifBlank { null },
                education = _state.value.educationTextState.text.toString().ifBlank { null },
                height = _state.value.height,
                zodiac = _state.value.zodiac,
                smoking = _state.value.smoking,
                drinking = _state.value.drinking,
                interests = _state.value.selectedInterests.ifEmpty { null }
            ).onSuccess { user ->
                _state.update {
                    it.copy(
                        isSavingProfile = false,
                        gender = user.gender,
                        birthDate = user.birthDate,
                        height = user.height,
                        zodiac = user.zodiac,
                        smoking = user.smoking,
                        drinking = user.drinking,
                        selectedInterests = user.interests,
                        showSuccessMessage = true
                    )
                }
                sessionStorage.observeAuthInfo().firstOrNull()?.let { info ->
                    sessionStorage.set(info.copy(user = user))
                }
            }.onFailure { error ->
                _state.update { it.copy(isSavingProfile = false, saveError = error.toUiText()) }
            }
        }
    }

    private fun toggleInterest(interest: String) {
        _state.update {
            val current = it.selectedInterests.toMutableList()
            if (current.contains(interest)) {
                current.remove(interest)
                it.copy(selectedInterests = current, interestsError = null)
            } else {
                if (current.size >= 10) {
                    it.copy(interestsError = "Máximo 10 intereses")
                } else {
                    current.add(interest)
                    it.copy(selectedInterests = current, interestsError = null)
                }
            }
        }
    }

    private fun showDeleteConfirmation(slotIndex: Int) {
        _state.update { it.copy(pendingDeleteSlot = slotIndex) }
    }

    private fun dismissDeleteConfirmation() {
        _state.update { it.copy(pendingDeleteSlot = null) }
    }

    private fun deletePhoto() {
        val slotIndex = _state.value.pendingDeleteSlot ?: return
        _state.update {
            it.copy(
                pendingDeleteSlot = null,
                deletingSlots = it.deletingSlots + slotIndex,
                imageError = null
            )
        }
        viewModelScope.launch {
            userService.deletePhoto(slotIndex)
                .onSuccess {
                    // Backend compacts the array — mirror that locally
                    val compacted = _state.value.photos.filterNotNull().toMutableList()
                    if (slotIndex < compacted.size) compacted.removeAt(slotIndex)
                    val updatedPhotos = List(6) { i -> compacted.getOrNull(i) }
                    _state.update {
                        it.copy(
                            deletingSlots = it.deletingSlots - slotIndex,
                            photos = updatedPhotos
                        )
                    }
                    updateSessionPhotos(compacted)
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            deletingSlots = it.deletingSlots - slotIndex,
                            imageError = error.toUiText()
                        )
                    }
                }
        }
    }

    private fun uploadPhoto(bytes: ByteArray, mimeType: String?, slotIndex: Int) {
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
                    // Place the confirmed URL at the target slot
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

    private fun onPhotoUploaded(slotIndex: Int, publicUrl: String) {
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

    private fun onPhotoUploadFailed(slotIndex: Int) {
        _state.update {
            it.copy(
                uploadingSlots = it.uploadingSlots - slotIndex,
                imageError = UiText.DynamicString("Error uploading photo")
            )
        }
    }

    private fun reorderPhotos(newPhotos: List<String?>) {
        val previousPhotos = _state.value.photos
        // Optimistic update — apply immediately, revert on failure
        _state.update { it.copy(photos = newPhotos) }
        val realPhotos = newPhotos.filterNotNull()
        viewModelScope.launch {
            userService.reorderPhotos(realPhotos)
                .onSuccess { updateSessionPhotos(realPhotos) }
                .onFailure { error ->
                    _state.update { it.copy(photos = previousPhotos, imageError = error.toUiText()) }
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
