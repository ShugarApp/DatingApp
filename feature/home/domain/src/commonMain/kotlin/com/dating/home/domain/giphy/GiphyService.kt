package com.dating.home.domain.giphy

import com.dating.core.domain.util.DataError
import com.dating.core.domain.util.Result

interface GiphyService {
    suspend fun trending(offset: Int = 0, limit: Int = 25): Result<List<GiphyGif>, DataError.Remote>
    suspend fun search(query: String, offset: Int = 0, limit: Int = 25): Result<List<GiphyGif>, DataError.Remote>
}
