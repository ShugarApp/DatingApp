package com.dating.home.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import com.dating.home.database.dao.ChatDao
import com.dating.home.database.dao.ChatMessageDao
import com.dating.home.database.dao.ChatParticipantDao
import com.dating.home.database.dao.ChatParticipantsCrossRefDao
import com.dating.home.database.entities.ChatEntity
import com.dating.home.database.entities.ChatMessageEntity
import com.dating.home.database.entities.ChatParticipantCrossRef
import com.dating.home.database.entities.ChatParticipantEntity
import com.dating.home.database.view.LastMessageView

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
abstract class AppChatDatabase : RoomDatabase() {
    abstract val chatDao: ChatDao
    abstract val chatParticipantDao: ChatParticipantDao
    abstract val chatMessageDao: ChatMessageDao
    abstract val chatParticipantsCrossRefDao: ChatParticipantsCrossRefDao

    companion object Companion {
        const val DB_NAME = "shugar.db"
    }
}
