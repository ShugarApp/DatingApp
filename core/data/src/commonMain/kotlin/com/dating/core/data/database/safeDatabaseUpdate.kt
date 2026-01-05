package com.dating.core.data.database

import androidx.sqlite.SQLiteException
import com.dating.core.domain.util.DataError
import com.dating.core.domain.util.Result

suspend inline fun <T> safeDatabaseUpdate(update: suspend () -> T): Result<T, DataError.Local> {
    return try {
        Result.Success(update())
    } catch(_: SQLiteException) {
        Result.Failure(DataError.Local.DISK_FULL)
    }
}