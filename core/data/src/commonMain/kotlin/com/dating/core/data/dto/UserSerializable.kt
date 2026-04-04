package com.dating.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserSerializable(
    val id: String,
    val email: String,
    val username: String,
    val hasVerifiedEmail: Boolean,
    val status: String = "PENDING",
    val isPaused: Boolean = false,
    val isIncognito: Boolean = false,
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
    // Optionally returned by the backend. When present, this value is authoritative
    // and should be used instead of the locally-computed profileCompletion().
    val profileCompletion: Int? = null
)
