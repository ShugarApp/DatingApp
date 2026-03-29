package com.dating.home.data.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.dating.home.data.chat.KtorChatService
import com.dating.home.data.chat.OfflineFirstChatRepository
import com.dating.home.data.chat.WebSocketChatConnectionClient
import com.dating.home.data.matching.KtorMatchingService
import com.dating.home.data.message.KtorChatMessageService
import com.dating.home.data.message.OfflineFirstMessageRepository
import com.dating.home.data.network.ConnectionRetryHandler
import com.dating.home.data.network.KtorWebSocketConnector
import com.dating.home.data.notification.KtorDeviceTokenService
import com.dating.home.data.participant.KtorChatParticipantService
import com.dating.home.data.participant.OfflineFirstChatParticipantRepository
import com.dating.home.data.block.KtorBlockService
import com.dating.home.data.report.KtorReportService
import com.dating.home.data.user.KtorUserService
import com.dating.home.database.DatabaseFactory
import com.dating.home.domain.chat.ChatConnectionClient
import com.dating.home.domain.chat.ChatRepository
import com.dating.home.domain.chat.ChatService
import com.dating.home.domain.matching.MatchingService
import com.dating.home.domain.message.ChatMessageService
import com.dating.home.domain.message.MessageRepository
import com.dating.home.domain.notification.DeviceTokenService
import com.dating.home.domain.participant.ChatParticipantRepository
import com.dating.home.domain.participant.ChatParticipantService
import com.dating.home.domain.block.BlockService
import com.dating.home.domain.report.ReportService
import com.dating.home.domain.user.UserService
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformChatDataModule: Module

val homeDataModule = module {
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
    singleOf(::KtorMatchingService) bind MatchingService::class
    singleOf(::KtorUserService) bind UserService::class
    singleOf(::KtorBlockService) bind BlockService::class
    singleOf(::KtorReportService) bind ReportService::class
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
