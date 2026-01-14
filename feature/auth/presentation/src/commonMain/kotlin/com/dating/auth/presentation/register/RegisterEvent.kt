package com.dating.auth.presentation.register

sealed interface RegisterEvent {
    data class Success(val email: String): RegisterEvent
    data object OnBack: RegisterEvent
}