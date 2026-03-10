package com.dating.home.presentation.profile.edit_profile

import androidx.compose.foundation.text.input.TextFieldState
import com.dating.core.presentation.util.UiText

data class EditProfileState(
    val profilePictureUrl: String? = null,
    val isUploadingImage: Boolean = false,
    val isDeletingImage: Boolean = false,
    val showDeleteConfirmationDialog: Boolean = false,
    val imageError: UiText? = null,
    val isSavingProfile: Boolean = false,
    val saveError: UiText? = null,
    val showSuccessMessage: Boolean = false,
    val bioError: String? = null,
    val birthDateError: String? = null,
    val heightError: String? = null,
    val interestsError: String? = null,

    // Profile Fields
    val gender: String? = null,       // "MALE" | "FEMALE" | "OTHER" | null
    val birthDate: String? = null,    // "YYYY-MM-DD" | null
    val bioTextState: TextFieldState = TextFieldState(),
    val jobTitleTextState: TextFieldState = TextFieldState(),
    val companyTextState: TextFieldState = TextFieldState(),
    val educationTextState: TextFieldState = TextFieldState(),
    val height: Int? = null,          // 100–250 cm
    val zodiac: String? = null,       // "ARIES" | "TAURUS" | ... | null
    val smoking: String? = null,      // "NEVER" | "SOMETIMES" | "REGULARLY" | null
    val drinking: String? = null,     // "NEVER" | "SOCIALLY" | "REGULARLY" | null
    val selectedInterests: List<String> = emptyList(), // API keys, max 10
    val availableInterests: List<String> = listOf(
        "photography", "hiking", "music", "cooking", "travel",
        "fitness", "reading", "gaming", "art", "dancing",
        "yoga", "movies", "sports", "technology", "fashion",
        "food", "coffee", "pets", "nature", "volunteering"
    ),

    val photos: List<String?> = listOf(null, null, null, null, null, null)
)
