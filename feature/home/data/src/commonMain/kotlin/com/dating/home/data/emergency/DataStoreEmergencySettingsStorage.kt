package com.dating.home.data.emergency

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.dating.home.domain.emergency.EmergencySettings
import com.dating.home.domain.emergency.EmergencySettingsStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class DataStoreEmergencySettingsStorage(
    private val dataStore: DataStore<Preferences>
) : EmergencySettingsStorage {

    private val enabledKey = booleanPreferencesKey("emergency_enabled")
    private val autoCall911Key = booleanPreferencesKey("emergency_auto_911")
    private val onboardingSeenKey = booleanPreferencesKey("emergency_onboarding_seen")

    override fun observe(): Flow<EmergencySettings> {
        return dataStore.data.map { prefs -> prefs.toEmergencySettings() }
    }

    override suspend fun get(): EmergencySettings {
        return dataStore.data.first().toEmergencySettings()
    }

    override suspend fun setEnabled(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[enabledKey] = enabled }
    }

    override suspend fun setAutoCall911(autoCall: Boolean) {
        dataStore.edit { prefs -> prefs[autoCall911Key] = autoCall }
    }

    override suspend fun setOnboardingSeen(seen: Boolean) {
        dataStore.edit { prefs -> prefs[onboardingSeenKey] = seen }
    }

    private fun Preferences.toEmergencySettings(): EmergencySettings {
        return EmergencySettings(
            isEnabled = this[enabledKey] ?: false,
            autoCall911 = this[autoCall911Key] ?: false,
            onboardingSeen = this[onboardingSeenKey] ?: false
        )
    }
}
