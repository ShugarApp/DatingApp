package com.dating.auth.presentation.login

sealed interface LoginEvent {
    data object Success : LoginEvent
    data class SuccessNewUser(val email: String) : LoginEvent
    data object OnBack : LoginEvent
}
