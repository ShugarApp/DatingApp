package com.dating.home.presentation.profile.edit_profile

import androidx.compose.foundation.text.input.TextFieldState
import com.dating.core.presentation.util.UiText

data class EditProfileState(
    val profilePictureUrl: String? = null,
    val isUploadingImage: Boolean = false,
    val isDeletingImage: Boolean = false,
    val showDeleteConfirmationDialog: Boolean = false,
    val imageError: UiText? = null,
    
    // Profile Fields
    val bioTextState: TextFieldState = TextFieldState(),
    val jobTitleTextState: TextFieldState = TextFieldState(),
    val companyTextState: TextFieldState = TextFieldState(),
    val educationTextState: TextFieldState = TextFieldState(),
    val locationTextState: TextFieldState = TextFieldState(),
    
    // Personal Info
    val heightTextState: TextFieldState = TextFieldState(),
    val zodiacTextState: TextFieldState = TextFieldState(),
    val smokingTextState: TextFieldState = TextFieldState(),
    val drinkingTextState: TextFieldState = TextFieldState(),
    
    val selectedInterests: List<String> = emptyList(),
    val availableInterests: List<String> = listOf(
        "Photography", "Hiking", "Art", "Sushi", "Travel", 
        "Running", "Design", "Music", "Cooking", "Reading",
        "Yoga", "Gaming", "Movies", "Tech", "Fashion"
    ),
    val photos: List<String?> = listOf(null, null, null, null, null, null) // Mock photo slots
)
