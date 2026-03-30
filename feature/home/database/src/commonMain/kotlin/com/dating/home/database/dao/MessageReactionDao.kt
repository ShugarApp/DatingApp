package com.dating.home.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.dating.home.database.entities.MessageReactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageReactionDao {

    @Query("SELECT * FROM message_reactions WHERE messageId = :messageId")
    fun getReactionsForMessage(messageId: String): Flow<List<MessageReactionEntity>>

    @Upsert
    suspend fun upsertReactions(reactions: List<MessageReactionEntity>)

    @Upsert
    suspend fun upsertReaction(reaction: MessageReactionEntity)

    @Query("DELETE FROM message_reactions WHERE messageId = :messageId")
    suspend fun deleteReactionsForMessage(messageId: String)

    @Query("DELETE FROM message_reactions WHERE id = :reactionId")
    suspend fun deleteReactionById(reactionId: String)

    @Query("""
        SELECT mr.* FROM message_reactions mr
        INNER JOIN chatmessageentity cm ON mr.messageId = cm.messageId
        WHERE cm.chatId = :chatId
    """)
    fun getReactionsForChat(chatId: String): Flow<List<MessageReactionEntity>>
}
