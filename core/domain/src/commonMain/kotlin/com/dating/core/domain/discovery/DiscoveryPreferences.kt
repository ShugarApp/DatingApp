package com.dating.core.domain.discovery

import kotlinx.coroutines.flow.Flow

data class DiscoverySettings(
    val maxDistance: Double? = null,
    val showMe: Gender = Gender.WOMEN,
    val minAge: Int = 18,
    val maxAge: Int = 50,
    val verifiedProfilesOnly: Boolean = false
)

enum class Gender(val value: String, val apiValue: String) {
    MEN("MEN", "MALE"),
    WOMEN("WOMEN", "FEMALE"),
    EVERYONE("EVERYONE", "OTHER");

    companion object {
        fun fromValue(value: String): Gender =
            entries.firstOrNull { it.value.equals(value, ignoreCase = true) } ?: WOMEN
    }
}

interface DiscoveryPreferencesStorage {
    fun observe(): Flow<DiscoverySettings>
    suspend fun get(): DiscoverySettings
    suspend fun updateMaxDistance(distance: Double?)
    suspend fun updateShowMe(gender: Gender)
    suspend fun updateAgeRange(minAge: Int, maxAge: Int)
    suspend fun updateVerifiedProfilesOnly(enabled: Boolean)
    suspend fun isCompleteProfilePromptShown(): Boolean
    suspend fun setCompleteProfilePromptShown()
    suspend fun clear()
}
