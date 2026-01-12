package com.dating.home.data.message

import com.dating.home.data.dto.ChatMessageDto
import com.dating.home.data.mappers.toDomain
import com.dating.home.domain.message.ChatMessageService
import com.dating.home.domain.models.ChatMessage
import com.dating.core.data.networking.delete
import com.dating.core.data.networking.get
import com.dating.core.domain.util.DataError
import com.dating.core.domain.util.EmptyResult
import com.dating.core.domain.util.Result
import com.dating.core.domain.util.map
import io.ktor.client.HttpClient

class KtorChatMessageService(
    private val httpClient: HttpClient
): ChatMessageService {

    override suspend fun deleteMessage(messageId: String): EmptyResult<DataError.Remote> {
        return httpClient.delete(
            route = "/messages/$messageId"
        )
    }

    override suspend fun fetchMessages(
        chatId: String,
        before: String?
    ): Result<List<ChatMessage>, DataError.Remote> {
        return httpClient.get<List<ChatMessageDto>>(
            route = "/chat/$chatId/messages",
            queryParams = buildMap {
                this["pageSize"] = ChatMessageConstants.PAGE_SIZE
                if(before != null) {
                    this["before"] = before
                }
            }
        ).map { it.map { it.toDomain() } }
    }
}