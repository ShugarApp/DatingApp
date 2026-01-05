package com.dating.chat.data.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.dating.chat.data.chat.KtorChatService
import com.dating.chat.data.chat.OfflineFirstChatRepository
import com.dating.chat.data.chat.WebSocketChatConnectionClient
import com.dating.chat.data.message.KtorChatMessageService
import com.dating.chat.data.message.OfflineFirstMessageRepository
import com.dating.chat.data.network.ConnectionRetryHandler
import com.dating.chat.data.network.KtorWebSocketConnector
import com.dating.chat.data.notification.KtorDeviceTokenService
import com.dating.chat.data.participant.KtorChatParticipantService
import com.dating.chat.data.participant.OfflineFirstChatParticipantRepository
import com.dating.chat.database.DatabaseFactory
import com.dating.chat.domain.chat.ChatConnectionClient
import com.dating.chat.domain.chat.ChatRepository
import com.dating.chat.domain.chat.ChatService
import com.dating.chat.domain.message.ChatMessageService
import com.dating.chat.domain.message.MessageRepository
import com.dating.chat.domain.notification.DeviceTokenService
import com.dating.chat.domain.participant.ChatParticipantRepository
import com.dating.chat.domain.participant.ChatParticipantService
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformChatDataModule: Module

val chatDataModule = module {
    includes(platformChatDataModule)

    singleOf(::KtorChatParticipantService) bind ChatParticipantService::class
    singleOf(::KtorChatService) bind ChatService::class
    singleOf(::OfflineFirstChatRepository) bind ChatRepository::class
    singleOf(::OfflineFirstMessageRepository) bind MessageRepository::class
    singleOf(::WebSocketChatConnectionClient) bind ChatConnectionClient::class
    singleOf(::ConnectionRetryHandler)
    singleOf(::KtorWebSocketConnector)
    singleOf(::KtorChatMessageService) bind ChatMessageService::class
    singleOf(::KtorDeviceTokenService) bind DeviceTokenService::class
    singleOf(::OfflineFirstChatParticipantRepository) bind ChatParticipantRepository::class
    single {
        Json {
            ignoreUnknownKeys = true
        }
    }
    single {
        get<DatabaseFactory>()
            .create()
            .setDriver(BundledSQLiteDriver())
            .build()
    }
}