package com.dating.home.presentation.profile.profile

import androidx.compose.foundation.text.input.TextFieldState

data class ProfileState(
    val username: String = "",
    val userInitials: String = "--",
    val profilePictureUrl: String? = null,
    val emailTextState: TextFieldState = TextFieldState()
)
