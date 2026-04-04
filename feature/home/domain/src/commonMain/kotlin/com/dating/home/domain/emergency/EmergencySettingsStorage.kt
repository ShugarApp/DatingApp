package com.dating.home.domain.emergency

import kotlinx.coroutines.flow.Flow

interface EmergencySettingsStorage {
    fun observe(): Flow<EmergencySettings>
    suspend fun get(): EmergencySettings
    suspend fun setEnabled(enabled: Boolean)
    suspend fun setAutoCall911(autoCall: Boolean)
    suspend fun setOnboardingSeen(seen: Boolean)
}
