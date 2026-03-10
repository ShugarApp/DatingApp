package com.dating.home.presentation.profile.profile

import androidx.compose.foundation.text.input.TextFieldState

data class ProfileState(
    val username: String = "",
    val userInitials: String = "--",
    val photos: List<String> = emptyList(),
    val emailTextState: TextFieldState = TextFieldState(),
    val city: String? = null,
    val country: String? = null,
    val bio: String? = null,
    val jobTitle: String? = null,
    val company: String? = null,
    val education: String? = null,
    val height: Int? = null,
    val zodiac: String? = null,
    val smoking: String? = null,
    val drinking: String? = null,
    val interests: List<String> = emptyList(),
    val isUpdatingLocation: Boolean = false,
    val locationError: String? = null,
    val showPreview: Boolean = false,
    val isLoadingPreview: Boolean = false
) {
    val profilePictureUrl: String? get() = photos.firstOrNull()
}
