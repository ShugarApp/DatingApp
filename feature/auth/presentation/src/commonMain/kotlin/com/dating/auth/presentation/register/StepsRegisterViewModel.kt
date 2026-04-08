package com.dating.auth.presentation.register

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aura.feature.auth.presentation.generated.resources.Res
import aura.feature.auth.presentation.generated.resources.error_account_exists
import aura.feature.auth.presentation.generated.resources.error_invalid_username
import aura.feature.auth.presentation.generated.resources.error_underage
import com.dating.core.domain.auth.AuthService
import com.dating.core.domain.util.DataError
import com.dating.core.domain.util.onFailure
import com.dating.core.domain.util.onSuccess
import com.dating.core.presentation.util.UiText
import com.dating.core.presentation.util.toUiText
import kotlin.time.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StepsRegisterViewModel(
    private val authService: AuthService,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val email: String = checkNotNull(savedStateHandle["email"])
    private val password: String = savedStateHandle["password"] ?: ""
    private val isGoogleUser: Boolean = savedStateHandle["isGoogleUser"] ?: false

    private val eventChannel = Channel<StepsRegisterEvent>()
    val events = eventChannel.receiveAsFlow()

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(StepsRegisterState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                observeValidationStates()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = StepsRegisterState()
        )

    private val isUsernameValidFlow = snapshotFlow { state.value.usernameTextState.text.toString() }
        .map { username -> username.length in 3..20 }
        .distinctUntilChanged()

    private val isRegisteringFlow = state
        .map { it.isRegistering }
        .distinctUntilChanged()

    private fun observeValidationStates() {
        combine(
            isUsernameValidFlow,
            isRegisteringFlow,
            state.map { it.currentStep }.distinctUntilChanged(),
            snapshotFlow { state.value.birthDateTextState.text.toString() }.distinctUntilChanged(),
            state.map { it.selectedGender },
            state.map { it.selectedInterest },
            state.map { it.selectedLookingFor },
            state.map { it.selectedIdealDate }
        ) { values ->
            val isUsernameValid = values[0] as Boolean
            val isRegistering = values[1] as Boolean
            val currentStep = values[2] as RegisterStep
            val birthDate = values[3] as String
            val gender = values[4] as String?
            val interest = values[5] as String?
            val lookingFor = values[6] as String?
            val idealDate = values[7] as String?

            _state.update {
                when (currentStep) {
                    RegisterStep.BasicInfo -> {
                        it.copy(canProceed = isUsernameValid)
                    }

                    RegisterStep.BirthDate -> {
                        val isFormatComplete = birthDate.length == 10
                        val isAgeValid = isFormatComplete && isAtLeast18YearsOld(birthDate)
                        val birthDateError = when {
                            isFormatComplete && !isAgeValid -> UiText.Resource(Res.string.error_underage)
                            else -> null
                        }
                        it.copy(canProceed = isAgeValid, birthDateError = birthDateError)
                    }

                    RegisterStep.GenderInterest -> {
                        it.copy(canProceed = gender != null && interest != null)
                    }

                    RegisterStep.LookingFor -> {
                        it.copy(canProceed = lookingFor != null)
                    }

                    RegisterStep.IdealDate -> {
                        it.copy(canRegister = !isRegistering && idealDate != null)
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun onAction(action: StepsRegisterAction) {
        when (action) {
            StepsRegisterAction.OnRegisterClick -> register()
            StepsRegisterAction.OnNextClick -> onNextClick()
            StepsRegisterAction.OnBackClick -> onBackClick()
            is StepsRegisterAction.OnGenderSelect -> {
                _state.update { it.copy(selectedGender = action.gender) }
                validateStep(RegisterStep.GenderInterest)
            }

            is StepsRegisterAction.OnInterestSelect -> {
                _state.update { it.copy(selectedInterest = action.interest) }
                validateStep(RegisterStep.GenderInterest)
            }

            is StepsRegisterAction.OnLookingForSelect -> {
                _state.update { it.copy(selectedLookingFor = action.lookingFor) }
                validateStep(RegisterStep.LookingFor)
            }

            is StepsRegisterAction.OnIdealDateSelect -> {
                _state.update { it.copy(selectedIdealDate = action.idealDate) }
                validateStep(RegisterStep.IdealDate)
            }

            StepsRegisterAction.OnInputTextFocusGain -> {
                _state.update { it.copy(registrationError = null, usernameError = null) }
            }
        }
    }

    private fun onNextClick() {
        val currentStep = state.value.currentStep
        if (validateStep(currentStep)) {
            val nextStep = when (currentStep) {
                RegisterStep.BasicInfo -> RegisterStep.BirthDate
                RegisterStep.BirthDate -> RegisterStep.GenderInterest
                RegisterStep.GenderInterest -> RegisterStep.LookingFor
                RegisterStep.LookingFor -> RegisterStep.IdealDate
                RegisterStep.IdealDate -> RegisterStep.IdealDate
            }
            _state.update { it.copy(currentStep = nextStep) }
        }
    }

    private fun onBackClick() {
        val currentStep = state.value.currentStep
        if (currentStep == RegisterStep.BasicInfo) {
            viewModelScope.launch {
                eventChannel.send(StepsRegisterEvent.OnBack)
            }
        } else {
            val previousStep = when (currentStep) {
                RegisterStep.BirthDate -> RegisterStep.BasicInfo
                RegisterStep.GenderInterest -> RegisterStep.BirthDate
                RegisterStep.LookingFor -> RegisterStep.GenderInterest
                RegisterStep.IdealDate -> RegisterStep.LookingFor
                else -> RegisterStep.BasicInfo
            }
            _state.update { it.copy(currentStep = previousStep) }
        }
    }

    private fun register() {
        if (!validateStep(RegisterStep.IdealDate)) {
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isRegistering = true) }

            val username = state.value.usernameTextState.text.toString()
            val birthDate = state.value.birthDateTextState.text.toString().toIsoDate()
            val gender = state.value.selectedGender
            val interest = state.value.selectedInterest
            val lookingFor = state.value.selectedLookingFor
            val idealDate = state.value.selectedIdealDate

            if (isGoogleUser) {
                // Google users are already registered, complete their profile
                authService
                    .completeProfile(
                        username = username,
                        birthDate = birthDate,
                        gender = gender ?: "",
                        interestedIn = interest ?: "",
                        lookingFor = lookingFor ?: "",
                        idealDate = idealDate
                    )
                    .onSuccess {
                        _state.update { it.copy(isRegistering = false) }
                        eventChannel.send(StepsRegisterEvent.GoogleSuccess)
                    }
                    .onFailure { error ->
                        _state.update {
                            it.copy(
                                isRegistering = false,
                                registrationError = error.toUiText(),
                            )
                        }
                    }
                return@launch
            }

            authService
                .register(
                    email = email,
                    username = username,
                    password = password,
                    birthDate = birthDate,
                    gender = gender,
                    interestedIn = interest,
                    lookingFor = lookingFor,
                    idealDate = idealDate
                )
                .onSuccess {
                    _state.update { it.copy(isRegistering = false) }
                    eventChannel.send(StepsRegisterEvent.Success(email))
                }
                .onFailure { error ->
                    val registrationError = when (error) {
                        DataError.Remote.CONFLICT -> UiText.Resource(Res.string.error_account_exists)
                        else -> error.toUiText()
                    }
                    _state.update {
                        it.copy(
                            isRegistering = false,
                            registrationError = registrationError,
                        )
                    }
                }
        }
    }

    private fun validateStep(step: RegisterStep): Boolean {
        val currentState = state.value

        return when (step) {
            RegisterStep.BasicInfo -> {
                val username = currentState.usernameTextState.text.toString()
                val isUsernameValid = username.length in 3..20
                val usernameError = if (!isUsernameValid) UiText.Resource(Res.string.error_invalid_username) else null
                _state.update { it.copy(usernameError = usernameError, canProceed = isUsernameValid) }
                isUsernameValid
            }

            RegisterStep.BirthDate -> {
                val birthDate = currentState.birthDateTextState.text.toString()
                val isFormatComplete = birthDate.length == 10
                val isAgeValid = isFormatComplete && isAtLeast18YearsOld(birthDate)
                val birthDateError = when {
                    isFormatComplete && !isAgeValid -> UiText.Resource(Res.string.error_underage)
                    else -> null
                }
                _state.update { it.copy(canProceed = isAgeValid, birthDateError = birthDateError) }
                isAgeValid
            }

            RegisterStep.GenderInterest -> {
                val hasGender = currentState.selectedGender != null
                val hasInterest = currentState.selectedInterest != null
                val isValid = hasGender && hasInterest
                _state.update { it.copy(canProceed = isValid) }
                isValid
            }

            RegisterStep.LookingFor -> {
                val hasLookingFor = currentState.selectedLookingFor != null
                _state.update { it.copy(canProceed = hasLookingFor) }
                hasLookingFor
            }

            RegisterStep.IdealDate -> {
                val hasIdealDate = currentState.selectedIdealDate != null
                _state.update { it.copy(canRegister = hasIdealDate) }
                hasIdealDate
            }
        }
    }
}

/**
 * Converts DD/MM/YYYY to YYYY-MM-DD (ISO 8601).
 */
private fun String.toIsoDate(): String {
    val parts = split("/")
    if (parts.size != 3) return this
    return "${parts[2]}-${parts[1]}-${parts[0]}"
}

/**
 * Returns true if the date (DD/MM/YYYY) corresponds to someone who is at least 18 years old.
 */
private fun isAtLeast18YearsOld(birthDateStr: String): Boolean {
    val parts = birthDateStr.split("/")
    if (parts.size != 3) return false
    val day = parts[0].toIntOrNull() ?: return false
    val month = parts[1].toIntOrNull() ?: return false
    val year = parts[2].toIntOrNull() ?: return false
    val birthDate = try {
        LocalDate(year, month, day)
    } catch (e: Exception) {
        return false
    }
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val age = today.year - birthDate.year
    val hasHadBirthdayThisYear = today.monthNumber > birthDate.monthNumber ||
        (today.monthNumber == birthDate.monthNumber && today.dayOfMonth >= birthDate.dayOfMonth)
    return (if (hasHadBirthdayThisYear) age else age - 1) >= 18
}
