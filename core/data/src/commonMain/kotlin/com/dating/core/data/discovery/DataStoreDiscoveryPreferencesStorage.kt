package com.dating.core.data.discovery

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.dating.core.domain.discovery.DiscoveryPreferencesStorage
import com.dating.core.domain.discovery.DiscoverySettings
import com.dating.core.domain.discovery.Gender
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class DataStoreDiscoveryPreferencesStorage(
    private val dataStore: DataStore<Preferences>
) : DiscoveryPreferencesStorage {

    private val maxDistanceKey = doublePreferencesKey("discovery_max_distance")
    private val showMeKey = stringPreferencesKey("discovery_show_me")
    private val minAgeKey = intPreferencesKey("discovery_min_age")
    private val maxAgeKey = intPreferencesKey("discovery_max_age")
    private val verifiedProfilesOnlyKey = booleanPreferencesKey("discovery_verified_profiles_only")
    private val completeProfilePromptShownKey = booleanPreferencesKey("complete_profile_prompt_shown")

    override fun observe(): Flow<DiscoverySettings> {
        return dataStore.data.map { prefs -> prefs.toDiscoverySettings() }
    }

    override suspend fun get(): DiscoverySettings {
        return dataStore.data.first().toDiscoverySettings()
    }

    override suspend fun updateMaxDistance(distance: Double?) {
        dataStore.edit { prefs ->
            if (distance != null) {
                prefs[maxDistanceKey] = distance
            } else {
                prefs.remove(maxDistanceKey)
            }
        }
    }

    override suspend fun updateShowMe(gender: Gender) {
        dataStore.edit { prefs ->
            prefs[showMeKey] = gender.value
        }
    }

    override suspend fun updateAgeRange(minAge: Int, maxAge: Int) {
        dataStore.edit { prefs ->
            prefs[minAgeKey] = minAge
            prefs[maxAgeKey] = maxAge
        }
    }

    override suspend fun updateVerifiedProfilesOnly(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[verifiedProfilesOnlyKey] = enabled
        }
    }

    override suspend fun isCompleteProfilePromptShown(): Boolean {
        return dataStore.data.first()[completeProfilePromptShownKey] ?: false
    }

    override suspend fun setCompleteProfilePromptShown() {
        dataStore.edit { prefs ->
            prefs[completeProfilePromptShownKey] = true
        }
    }

    private fun Preferences.toDiscoverySettings(): DiscoverySettings {
        return DiscoverySettings(
            maxDistance = this[maxDistanceKey],
            showMe = this[showMeKey]?.let { Gender.fromValue(it) } ?: Gender.WOMEN,
            minAge = this[minAgeKey] ?: 18,
            maxAge = this[maxAgeKey] ?: 50,
            verifiedProfilesOnly = this[verifiedProfilesOnlyKey] ?: false
        )
    }
}
