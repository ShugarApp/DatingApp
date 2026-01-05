package com.dating.chat.database

import androidx.room.RoomDatabaseConstructor

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object ChirpChatDatabaseConstructor: RoomDatabaseConstructor<AppChatDatabase> {
    override fun initialize(): AppChatDatabase
}