package com.dating.home.domain.chat

import com.dating.home.domain.models.ChatMessage
import com.dating.home.domain.models.ConnectionState
import com.dating.home.domain.models.TypingIndicator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ChatConnectionClient {
    val chatMessages: Flow<ChatMessage>
    val connectionState: StateFlow<ConnectionState>
    val typingIndicators: Flow<TypingIndicator>
    suspend fun sendTyping(chatId: String)
}