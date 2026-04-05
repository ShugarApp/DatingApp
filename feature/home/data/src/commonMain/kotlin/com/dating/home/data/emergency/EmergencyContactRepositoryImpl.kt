package com.dating.home.data.emergency

import com.dating.home.database.AppChatDatabase
import com.dating.home.database.entities.EmergencyContactEntity
import com.dating.home.domain.emergency.EmergencyContact
import com.dating.home.domain.emergency.EmergencyContactRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

class EmergencyContactRepositoryImpl(
    private val database: AppChatDatabase
) : EmergencyContactRepository {

    override fun getAll(): Flow<List<EmergencyContact>> {
        return database.emergencyContactDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun add(contact: EmergencyContact) {
        database.emergencyContactDao.upsert(contact.toEntity())
    }

    override suspend fun update(contact: EmergencyContact) {
        database.emergencyContactDao.upsert(contact.toEntity())
    }

    override suspend fun delete(contactId: String) {
        database.emergencyContactDao.deleteById(contactId)
    }

    override suspend fun getCount(): Int {
        return database.emergencyContactDao.getCount()
    }

    private fun EmergencyContactEntity.toDomain(): EmergencyContact {
        return EmergencyContact(
            id = id,
            name = name,
            phoneNumber = phoneNumber,
            relationship = relationship
        )
    }

    private fun EmergencyContact.toEntity(): EmergencyContactEntity {
        return EmergencyContactEntity(
            id = id,
            name = name,
            phoneNumber = phoneNumber,
            relationship = relationship,
            createdAt = Clock.System.now().toEpochMilliseconds()
        )
    }
}
