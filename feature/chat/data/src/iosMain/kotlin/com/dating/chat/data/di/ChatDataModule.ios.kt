package com.dating.chat.data.di

import com.dating.chat.data.lifecycle.AppLifecycleObserver
import com.dating.chat.data.network.ConnectionErrorHandler
import com.dating.chat.data.network.ConnectivityObserver
import com.dating.chat.data.notification.FirebasePushNotificationService
import com.dating.chat.database.DatabaseFactory
import com.dating.chat.domain.notification.PushNotificationService
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformChatDataModule = module {
    single { DatabaseFactory() }
    singleOf(::AppLifecycleObserver)
    singleOf(::ConnectivityObserver)
    singleOf(::ConnectionErrorHandler)
    singleOf(::FirebasePushNotificationService) bind PushNotificationService::class
}