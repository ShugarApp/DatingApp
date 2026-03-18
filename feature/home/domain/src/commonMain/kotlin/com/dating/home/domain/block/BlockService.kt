package com.dating.home.domain.block

import com.dating.core.domain.util.DataError
import com.dating.core.domain.util.EmptyResult
import com.dating.core.domain.util.Result

interface BlockService {
    suspend fun blockUser(userId: String): EmptyResult<DataError.Remote>
    suspend fun unblockUser(userId: String): EmptyResult<DataError.Remote>
    suspend fun getBlockedUsers(): Result<List<BlockedUser>, DataError.Remote>
}
