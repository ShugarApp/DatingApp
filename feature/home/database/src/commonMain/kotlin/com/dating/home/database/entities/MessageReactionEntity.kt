package com.dating.home.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "message_reactions",
    foreignKeys = [
        ForeignKey(
            entity = ChatMessageEntity::class,
            parentColumns = ["messageId"],
            childColumns = ["messageId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("messageId"),
        Index(value = ["messageId", "emoji"], unique = true)
    ]
)
data class MessageReactionEntity(
    @PrimaryKey
    val id: String, // "${messageId}_${emoji}"
    val messageId: String,
    val emoji: String,
    val count: Int,
    val reactedByMe: Boolean
)
