package com.dating.home.presentation.profile.verification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dating.core.domain.auth.SessionStorage
import com.dating.core.domain.auth.VerificationStatus
import com.dating.core.domain.util.Result
import com.dating.core.domain.util.onFailure
import com.dating.core.domain.util.onSuccess
import com.dating.home.domain.user.UserService
import com.dating.home.presentation.profile.mediapicker.PickedImageData
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VerificationViewModel(
    private val userService: UserService,
    private val sessionStorage: SessionStorage
) : ViewModel() {

    private val _state = MutableStateFlow(VerificationState())
    val state = _state.asStateFlow()

    private val _events = Channel<VerificationEvent>()
    val events = _events.receiveAsFlow()

    init {
        loadStatus()
    }

    private fun loadStatus() {
        viewModelScope.launch {
            val authInfo = sessionStorage.observeAuthInfo().firstOrNull()
            val sessionStatus = authInfo?.user?.verificationStatus ?: VerificationStatus.UNVERIFIED
            _state.update { it.copy(verificationStatus = sessionStatus) }

            // Fetch latest status from backend when already in a non-unverified state
            if (sessionStatus != VerificationStatus.UNVERIFIED) {
                _state.update { it.copy(isLoading = true) }
                userService.getVerificationStatus()
                    .onSuccess { verification ->
                        if (verification != null) {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    verification = verification,
                                    verificationStatus = verification.status
                                )
                            }
                        } else {
                            _state.update { it.copy(isLoading = false) }
                        }
                    }
                    .onFailure {
                        _state.update { it.copy(isLoading = false) }
                    }
            }
        }
    }

    fun onAction(action: VerificationAction) {
        when (action) {
            VerificationAction.OnBack -> viewModelScope.launch {
                _events.send(VerificationEvent.NavigateBack)
            }
            VerificationAction.OnStartCapture -> {
                _state.update { it.copy(step = VerificationStep.GUIDANCE, error = null) }
            }
            is VerificationAction.OnSelfieCaptured -> submitSelfie(action.imageData)
            VerificationAction.OnRetry -> {
                _state.update { it.copy(step = VerificationStep.GUIDANCE, error = null) }
            }
            VerificationAction.OnDismissError -> {
                _state.update { it.copy(error = null) }
            }
            is VerificationAction.OnPermissionDenied -> {
                _state.update {
                    it.copy(
                        error = if (action.permanently) {
                            "Camera permission was permanently denied. Please enable it in your device settings."
                        } else {
                            "Camera permission is required to take a selfie."
                        }
                    )
                }
            }
        }
    }

    private fun submitSelfie(imageData: PickedImageData) {
        viewModelScope.launch {
            _state.update { it.copy(step = VerificationStep.PROCESSING, isProcessing = true) }

            val uploadResult = userService.uploadSelfie(
                imageBytes = imageData.bytes,
                mimeType = imageData.mimeType ?: "image/jpeg"
            )

            if (uploadResult is Result.Failure) {
                _state.update {
                    it.copy(
                        isProcessing = false,
                        step = VerificationStep.GUIDANCE,
                        error = "Could not upload selfie. Please try again."
                    )
                }
                return@launch
            }

            val selfieUrl = (uploadResult as Result.Success).data

            when (val verifyResult = userService.submitVerification(selfieUrl)) {
                is Result.Success -> {
                    val verification = verifyResult.data
                    persistVerificationStatusToSession(verification.status)
                    _state.update {
                        it.copy(
                            isProcessing = false,
                            step = VerificationStep.RESULT,
                            verification = verification,
                            verificationStatus = verification.status
                        )
                    }
                }
                is Result.Failure -> {
                    val isTooManyRequests = verifyResult.error ==
                        com.dating.core.domain.util.DataError.Remote.TOO_MANY_REQUESTS
                    _state.update {
                        it.copy(
                            isProcessing = false,
                            step = VerificationStep.GUIDANCE,
                            error = if (isTooManyRequests) {
                                "Maximum attempts reached for today. Try again tomorrow."
                            } else {
                                "Verification failed. Please try again."
                            }
                        )
                    }
                }
            }
        }
    }

    private suspend fun persistVerificationStatusToSession(status: VerificationStatus) {
        val authInfo = sessionStorage.observeAuthInfo().firstOrNull() ?: return
        sessionStorage.set(
            authInfo.copy(user = authInfo.user.copy(verificationStatus = status))
        )
    }
}
