package com.dating.chat.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import com.dating.chat.database.dao.ChatDao
import com.dating.chat.database.dao.ChatMessageDao
import com.dating.chat.database.dao.ChatParticipantDao
import com.dating.chat.database.dao.ChatParticipantsCrossRefDao
import com.dating.chat.database.entities.ChatEntity
import com.dating.chat.database.entities.ChatMessageEntity
import com.dating.chat.database.entities.ChatParticipantCrossRef
import com.dating.chat.database.entities.ChatParticipantEntity
import com.dating.chat.database.view.LastMessageView

@Database(
    entities = [
        ChatEntity::class,
        ChatParticipantEntity::class,
        ChatMessageEntity::class,
        ChatParticipantCrossRef::class,
    ],
    views = [
        LastMessageView::class
    ],
    version = 1,
)
@ConstructedBy(ChirpChatDatabaseConstructor::class)
abstract class AppChatDatabase: RoomDatabase() {
    abstract val chatDao: ChatDao
    abstract val chatParticipantDao: ChatParticipantDao
    abstract val chatMessageDao: ChatMessageDao
    abstract val chatParticipantsCrossRefDao: ChatParticipantsCrossRefDao

    companion object Companion {
        const val DB_NAME = "aura.db"
    }
}