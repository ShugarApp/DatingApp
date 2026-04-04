package com.dating.core.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.dating.core.domain.preferences.OnboardingPreferences
import kotlinx.coroutines.flow.first

class DataStoreOnboardingPreferences(
    private val dataStore: DataStore<Preferences>
) : OnboardingPreferences {

    private val featuresOnboardingSeenKey = booleanPreferencesKey("features_onboarding_seen")

    override suspend fun hasSeenFeaturesOnboarding(): Boolean {
        return dataStore.data.first()[featuresOnboardingSeenKey] ?: false
    }

    override suspend fun markFeaturesOnboardingSeen() {
        dataStore.edit { preferences ->
            preferences[featuresOnboardingSeenKey] = true
        }
    }
}
