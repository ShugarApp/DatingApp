package com.dating.auth.presentation.register

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.snapshotFlow
import aura.feature.auth.presentation.generated.resources.Res
import aura.feature.auth.presentation.generated.resources.error_account_exists
import aura.feature.auth.presentation.generated.resources.error_invalid_username
import com.dating.core.domain.auth.AuthService
import com.dating.core.domain.util.DataError
import com.dating.core.domain.util.onFailure
import com.dating.core.domain.util.onSuccess
import com.dating.core.presentation.util.UiText
import com.dating.core.presentation.util.toUiText
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
    private val password: String = checkNotNull(savedStateHandle["password"])

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
            state.map { it.selectedLookingFor }
        ) { values ->
            val isUsernameValid = values[0] as Boolean
            val isRegistering = values[1] as Boolean
            val currentStep = values[2] as RegisterStep
            val birthDate = values[3] as String
            val gender = values[4] as String?
            val interest = values[5] as String?
            val lookingFor = values[6] as String?

            _state.update { 
                when(currentStep) {
                    RegisterStep.BasicInfo -> {
                        it.copy(canProceed = isUsernameValid)
                    }
                    RegisterStep.BirthDate -> {
                        it.copy(canProceed = birthDate.length == 10)
                    }
                    RegisterStep.GenderInterest -> {
                        it.copy(canProceed = gender != null && interest != null)
                    }
                    RegisterStep.LookingFor -> {
                         it.copy(canRegister = !isRegistering && lookingFor != null)
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
            StepsRegisterAction.OnInputTextFocusGain -> {
                _state.update { it.copy(registrationError = null, usernameError = null) }
            }
        }
    }

    private fun onNextClick() {
        val currentStep = state.value.currentStep
        if (validateStep(currentStep)) {
            val nextStep = when(currentStep) {
                RegisterStep.BasicInfo -> RegisterStep.BirthDate
                RegisterStep.BirthDate -> RegisterStep.GenderInterest
                RegisterStep.GenderInterest -> RegisterStep.LookingFor
                RegisterStep.LookingFor -> RegisterStep.LookingFor
                else -> RegisterStep.BasicInfo
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
             val previousStep = when(currentStep) {
                RegisterStep.BirthDate -> RegisterStep.BasicInfo
                RegisterStep.GenderInterest -> RegisterStep.BirthDate
                RegisterStep.LookingFor -> RegisterStep.GenderInterest
                else -> RegisterStep.BasicInfo
            }
            _state.update { it.copy(currentStep = previousStep) }
        }
    }

    private fun register() {
         if (!validateStep(RegisterStep.LookingFor)) {
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isRegistering = true) }

            val username = state.value.usernameTextState.text.toString()

            authService
                .register(
                    email = email,
                    username = username,
                    password = password
                )
                .onSuccess {
                    _state.update { it.copy(isRegistering = false) }
                    eventChannel.send(StepsRegisterEvent.Success(email))
                }
                .onFailure { error ->
                    val registrationError = when(error) {
                        DataError.Remote.CONFLICT -> UiText.Resource(Res.string.error_account_exists)
                        else -> error.toUiText()
                    }
                    _state.update { it.copy(
                        isRegistering = false,
                        registrationError = registrationError,
                    ) }
                }
        }
    }

    private fun validateStep(step: RegisterStep): Boolean {
        val currentState = state.value
        
        return when(step) {
            RegisterStep.BasicInfo -> {
                val username = currentState.usernameTextState.text.toString()
                val isUsernameValid = username.length in 3..20
                val usernameError = if (!isUsernameValid) UiText.Resource(Res.string.error_invalid_username) else null
                _state.update { it.copy(usernameError = usernameError, canProceed = isUsernameValid) }
                isUsernameValid
            }
            RegisterStep.BirthDate -> {
                val birthDate = currentState.birthDateTextState.text.toString()
                val isValid = birthDate.length == 10
                _state.update { it.copy(canProceed = isValid) }
                isValid
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
                 val isValid = hasLookingFor
                 _state.update { it.copy(canRegister = isValid) }
                 isValid
            }
        }
    }
}
