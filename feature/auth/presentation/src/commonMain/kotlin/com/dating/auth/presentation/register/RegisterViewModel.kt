package com.dating.auth.presentation.register

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aura.feature.auth.presentation.generated.resources.Res
import aura.feature.auth.presentation.generated.resources.error_account_exists
import aura.feature.auth.presentation.generated.resources.error_invalid_email
import aura.feature.auth.presentation.generated.resources.error_invalid_password
import aura.feature.auth.presentation.generated.resources.error_invalid_username
import com.dating.core.domain.auth.AuthService
import com.dating.core.domain.util.DataError
import com.dating.core.domain.util.onFailure
import com.dating.core.domain.util.onSuccess
import com.dating.core.domain.validation.PasswordValidator
import com.dating.core.presentation.util.UiText
import com.dating.core.presentation.util.toUiText
import com.dating.domain.EmailValidator
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

class RegisterViewModel(
    private val authService: AuthService
) : ViewModel() {

    private val eventChannel = Channel<RegisterEvent>()
    val events = eventChannel.receiveAsFlow()

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(RegisterState())
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
            initialValue = RegisterState()
        )

    private val isEmailValidFlow = snapshotFlow { state.value.emailTextState.text.toString() }
        .map { email -> EmailValidator.validate(email) }
        .distinctUntilChanged()

    private val isUsernameValidFlow = snapshotFlow { state.value.usernameTextState.text.toString() }
        .map { username -> username.length in 3..20 }
        .distinctUntilChanged()

    private val isPasswordValidFlow = snapshotFlow { state.value.passwordTextState.text.toString() }
        .map { password -> PasswordValidator.validate(password).isValidPassword }
        .distinctUntilChanged()

    private val isRegisteringFlow = state
        .map { it.isRegistering }
        .distinctUntilChanged()

    private fun observeValidationStates() {
        combine(
            isEmailValidFlow,
            isUsernameValidFlow,
            isPasswordValidFlow,
            isRegisteringFlow,
            state.map { it.currentStep }.distinctUntilChanged(),
            snapshotFlow { state.value.nameTextState.text.toString() }.distinctUntilChanged(),
            snapshotFlow { state.value.birthDateTextState.text.toString() }.distinctUntilChanged(),
            state.map { it.selectedGender }, // can't use distinctUntilChanged easily on nullable, or maybe ok
            state.map { it.selectedInterest },
            state.map { it.selectedLookingFor }
        ) { values ->
            val isEmailValid = values[0] as Boolean
            val isUsernameValid = values[1] as Boolean
            val isPasswordValid = values[2] as Boolean
            val isRegistering = values[3] as Boolean
            val currentStep = values[4] as RegisterStep
            val name = values[5] as String
            val birthDate = values[6] as String
            val gender = values[7] as String?
            val interest = values[8] as String?
            val lookingFor = values[9] as String?

            _state.update { 
                when(currentStep) {
                    RegisterStep.Credentials -> {
                         it.copy(canProceed = isEmailValid && isPasswordValid)
                    }
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

    fun onAction(action: RegisterAction) {
        when (action) {
            RegisterAction.OnLoginClick -> Unit
            RegisterAction.OnRegisterClick -> register()
            RegisterAction.OnNextClick -> onNextClick()
            RegisterAction.OnBackClick -> onBackClick()
            RegisterAction.OnTogglePasswordVisibilityClick -> {
                _state.update { it.copy(
                    isPasswordVisible = !it.isPasswordVisible
                ) }
            }
            is RegisterAction.OnGenderSelect -> {
                _state.update { it.copy(selectedGender = action.gender) }
                validateStep(RegisterStep.GenderInterest)
            }
            is RegisterAction.OnInterestSelect -> {
                _state.update { it.copy(selectedInterest = action.interest) }
                validateStep(RegisterStep.GenderInterest)
            }
            is RegisterAction.OnLookingForSelect -> {
                _state.update { it.copy(selectedLookingFor = action.lookingFor) }
                validateStep(RegisterStep.LookingFor)
            }
            else -> Unit
        }
    }


    private fun onNextClick() {
        val currentStep = state.value.currentStep
        if (validateStep(currentStep)) {
            val nextStep = when(currentStep) {
                RegisterStep.Credentials -> RegisterStep.BasicInfo
                RegisterStep.BasicInfo -> RegisterStep.BirthDate
                RegisterStep.BirthDate -> RegisterStep.GenderInterest
                RegisterStep.GenderInterest -> RegisterStep.LookingFor
                RegisterStep.LookingFor -> RegisterStep.LookingFor // Max step
            }
            _state.update { it.copy(currentStep = nextStep) }
            validateStep(nextStep) // Validate next step initial state
        }
    }

    private fun onBackClick() {
        val currentStep = state.value.currentStep
        if (currentStep == RegisterStep.Credentials) {
            viewModelScope.launch {
                eventChannel.send(RegisterEvent.OnBack)
            }
        } else {
             val previousStep = when(currentStep) {
                RegisterStep.BasicInfo -> RegisterStep.Credentials
                RegisterStep.BirthDate -> RegisterStep.BasicInfo
                RegisterStep.GenderInterest -> RegisterStep.BirthDate
                RegisterStep.LookingFor -> RegisterStep.GenderInterest
                else -> RegisterStep.Credentials
            }
            _state.update { it.copy(currentStep = previousStep) }
        }
    }

    private fun register() {
         // Final validation checks everything
         if (!validateStep(RegisterStep.LookingFor)) { // Ensure last step is valid
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(
                isRegistering = true,
            ) }

            val email = state.value.emailTextState.text.toString()
            val username = state.value.usernameTextState.text.toString()
            val password = state.value.passwordTextState.text.toString()

            authService
                .register(
                    email = email,
                    username = username,
                    password = password
                )
                .onSuccess {
                    _state.update { it.copy(
                        isRegistering = false,
                    ) }
                    eventChannel.send(RegisterEvent.Success(email))
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

    private fun clearAllTextFieldErrors() {
        _state.update {
            it.copy(
                emailError = null,
                usernameError = null,
                passwordError = null,
                registrationError = null
            )
        }
    }

    private fun validateStep(step: RegisterStep): Boolean {
        // We only clear errors that are relevant to the current or previous validation attempt
        // actually clearing all might be too aggressive if we want to keep state errors, 
        // but for now it resets "dirty" error states.
        clearAllTextFieldErrors()
        
        val currentState = state.value
        
        return when(step) {
            RegisterStep.Credentials -> {
                val email = currentState.emailTextState.text.toString()
                val password = currentState.passwordTextState.text.toString()
                val isEmailValid = EmailValidator.validate(email)
                val passwordValidationState = PasswordValidator.validate(password)
                
                 val emailError = if (!isEmailValid) UiText.Resource(Res.string.error_invalid_email) else null
                 val passwordError = if (!passwordValidationState.isValidPassword) UiText.Resource(Res.string.error_invalid_password) else null
                 
                 _state.update { it.copy(emailError = emailError, passwordError = passwordError, canProceed = isEmailValid && passwordValidationState.isValidPassword) }
                 isEmailValid && passwordValidationState.isValidPassword
            }
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
    
    // Helper to reactively validate without setting errors immediately (optional optimization)
    // For now we reuse validateStep but rely on the `observeValidationStates` flow for real-time button updates
    private fun checkValidation(step: RegisterStep) {
        // Logic similar to validateStep but without setting errors, just updating canProceed
        // ... omitted for brevity, we will rely on key events calling checkValidation or validateStep
        // Or better yet, updated observeValidationStates
    }
}