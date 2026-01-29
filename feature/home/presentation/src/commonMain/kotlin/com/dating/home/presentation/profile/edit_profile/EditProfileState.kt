package com.dating.home.presentation.profile.edit_profile

import androidx.compose.foundation.text.input.TextFieldState
import shugar.feature.home.presentation.generated.resources.Res
import shugar.feature.home.presentation.generated.resources.interest_art
import shugar.feature.home.presentation.generated.resources.interest_cooking
import shugar.feature.home.presentation.generated.resources.interest_design
import shugar.feature.home.presentation.generated.resources.interest_fashion
import shugar.feature.home.presentation.generated.resources.interest_gaming
import shugar.feature.home.presentation.generated.resources.interest_hiking
import shugar.feature.home.presentation.generated.resources.interest_movies
import shugar.feature.home.presentation.generated.resources.interest_music
import shugar.feature.home.presentation.generated.resources.interest_photography
import shugar.feature.home.presentation.generated.resources.interest_reading
import shugar.feature.home.presentation.generated.resources.interest_running
import shugar.feature.home.presentation.generated.resources.interest_sushi
import shugar.feature.home.presentation.generated.resources.interest_tech
import shugar.feature.home.presentation.generated.resources.interest_travel
import shugar.feature.home.presentation.generated.resources.interest_yoga
import com.dating.core.presentation.util.UiText
import org.jetbrains.compose.resources.StringResource

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

    val selectedInterests: List<StringResource> = emptyList(),
    val availableInterests: List<StringResource> = listOf(
        Res.string.interest_photography, Res.string.interest_hiking, Res.string.interest_art, Res.string.interest_sushi, Res.string.interest_travel,
        Res.string.interest_running, Res.string.interest_design, Res.string.interest_music, Res.string.interest_cooking, Res.string.interest_reading,
        Res.string.interest_yoga, Res.string.interest_gaming, Res.string.interest_movies, Res.string.interest_tech, Res.string.interest_fashion
    ),
    val photos: List<String?> = listOf(null, null, null, null, null, null) // Mock photo slots
)
