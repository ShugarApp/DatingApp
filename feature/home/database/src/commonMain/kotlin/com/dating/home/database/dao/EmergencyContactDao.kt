package com.dating.home.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.dating.home.database.entities.EmergencyContactEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EmergencyContactDao {

    @Upsert
    suspend fun upsert(contact: EmergencyContactEntity)

    @Query("DELETE FROM emergencycontactentity WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM emergencycontactentity ORDER BY createdAt ASC")
    fun getAll(): Flow<List<EmergencyContactEntity>>

    @Query("SELECT COUNT(*) FROM emergencycontactentity")
    suspend fun getCount(): Int
}
