package com.dating.home.domain.emergency

import kotlinx.coroutines.flow.Flow

interface EmergencyContactRepository {
    fun getAll(): Flow<List<EmergencyContact>>
    suspend fun add(contact: EmergencyContact)
    suspend fun update(contact: EmergencyContact)
    suspend fun delete(contactId: String)
    suspend fun getCount(): Int
}
