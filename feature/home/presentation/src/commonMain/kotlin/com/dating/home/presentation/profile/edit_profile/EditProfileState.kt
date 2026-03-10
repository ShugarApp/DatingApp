package com.dating.home.presentation.profile.edit_profile

import androidx.compose.foundation.text.input.TextFieldState
import com.dating.core.presentation.util.UiText

data class EditProfileState(
    // Per-slot loading: which photo indices are being uploaded or deleted
    val uploadingSlots: Set<Int> = emptySet(),
    val deletingSlots: Set<Int> = emptySet(),
    // Which slot the pending delete confirmation is targeting (null = no dialog)
    val pendingDeleteSlot: Int? = null,
    val imageError: UiText? = null,

    val isSavingProfile: Boolean = false,
    val saveError: UiText? = null,
    val showSuccessMessage: Boolean = false,

    val bioError: String? = null,
    val birthDateError: String? = null,
    val heightError: String? = null,
    val interestsError: String? = null,

    // Profile Fields
    val gender: String? = null,
    val birthDate: String? = null,
    val bioTextState: TextFieldState = TextFieldState(),
    val jobTitleTextState: TextFieldState = TextFieldState(),
    val companyTextState: TextFieldState = TextFieldState(),
    val educationTextState: TextFieldState = TextFieldState(),
    val height: Int? = null, // 100–250 cm
    val zodiac: String? = null,
    val smoking: String? = null,
    val drinking: String? = null,
    val selectedInterests: List<String> = emptyList(),
    val availableInterests: List<String> = listOf(
        "photography", "hiking", "music", "cooking", "travel",
        "fitness", "reading", "gaming", "art", "dancing",
        "yoga", "movies", "sports", "technology", "fashion",
        "food", "coffee", "pets", "nature", "volunteering"
    ),

    // 6 photo slots; null = empty
    val photos: List<String?> = List(6) { null }
) {
    val showDeleteConfirmationDialog: Boolean get() = pendingDeleteSlot != null
}
