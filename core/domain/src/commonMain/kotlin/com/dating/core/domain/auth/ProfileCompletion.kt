package com.dating.core.domain.auth

/**
 * Computes profile completion as a percentage (0–100).
 *
 * Each of the 10 key fields is worth 10 points.
 * When the backend starts returning a [UserSerializable.profileCompletion] value it will
 * be stored alongside the rest of the User and can override this locally-computed value.
 */
fun User.profileCompletion(): Int {
    var score = 0
    if (photos.isNotEmpty()) score += 10
    if (!bio.isNullOrBlank()) score += 10
    if (interests.isNotEmpty()) score += 10
    if (gender != null) score += 10
    if (birthDate != null) score += 10
    if (jobTitle != null || company != null || education != null) score += 10
    if (height != null) score += 10
    if (zodiac != null) score += 10
    if (smoking != null || drinking != null) score += 10
    if (verificationStatus == VerificationStatus.VERIFIED) score += 10
    return score
}

/**
 * Returns the list of profile field keys that are still empty.
 * Keys match the string resource names used by the UI (e.g. "bio", "interests").
 */
fun User.missingProfileFields(): List<String> = buildList {
    if (photos.isEmpty()) add("photos")
    if (bio.isNullOrBlank()) add("bio")
    if (interests.isEmpty()) add("interests")
    if (gender == null) add("gender")
    if (birthDate == null) add("birthDate")
    if (jobTitle == null && company == null && education == null) add("work")
    if (height == null) add("height")
    if (zodiac == null) add("zodiac")
    if (smoking == null && drinking == null) add("lifestyle")
    if (verificationStatus != VerificationStatus.VERIFIED) add("verification")
}
