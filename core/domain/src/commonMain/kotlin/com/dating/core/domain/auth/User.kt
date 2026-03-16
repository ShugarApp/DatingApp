package com.dating.core.domain.auth

data class User(
    val id: String,
    val email: String,
    val username: String,
    val hasVerifiedEmail: Boolean,
    val photos: List<String> = emptyList(),
    val city: String? = null,
    val country: String? = null,
    val bio: String? = null,
    val gender: String? = null,
    val birthDate: String? = null,
    val jobTitle: String? = null,
    val company: String? = null,
    val education: String? = null,
    val height: Int? = null,
    val zodiac: String? = null,
    val smoking: String? = null,
    val drinking: String? = null,
    val interests: List<String> = emptyList(),
    val isPaused: Boolean = false
) {
    val profilePictureUrl: String? get() = photos.firstOrNull()
}
