package com.dating.home.presentation.photo_onboarding

import com.dating.core.presentation.util.UiText

data class PhotoOnboardingState(
    val photos: List<String?> = List(4) { null },
    val uploadingSlots: Set<Int> = emptySet(),
    val imageError: UiText? = null,
    val isCompleting: Boolean = false
) {
    val uploadedCount: Int get() = photos.count { it != null }
    val hasMinimumPhotos: Boolean get() = uploadedCount >= 2
}
