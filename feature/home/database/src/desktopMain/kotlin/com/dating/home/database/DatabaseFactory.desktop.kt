package com.dating.home.database

import androidx.room.Room
import androidx.room.RoomDatabase
import com.dating.core.data.util.appDataDirectory
import java.io.File

actual class DatabaseFactory {
    actual fun create(): RoomDatabase.Builder<AppChatDatabase> {
        val directory = appDataDirectory

        if(!directory.exists()) {
            directory.mkdirs()
        }

        val dbFile = File(directory, AppChatDatabase.DB_NAME)
        return Room.databaseBuilder(dbFile.absolutePath)
    }
}