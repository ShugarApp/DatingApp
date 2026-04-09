package com.dating.home.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfileRequest(
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
    val interests: List<String>? = null,
    val idealDate: String? = null,
    val interestedIn: String? = null,
    val lookingFor: String? = null
)
