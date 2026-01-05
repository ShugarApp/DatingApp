package com.dating.auth.presentation.login

sealed interface LoginEvent {
    data object Success: LoginEvent
}