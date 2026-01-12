package com.dating.home.data.di

import com.dating.home.data.lifecycle.AppLifecycleObserver
import com.dating.home.data.network.ConnectionErrorHandler
import com.dating.home.data.network.ConnectivityObserver
import com.dating.home.data.notification.FirebasePushNotificationService
import com.dating.home.database.DatabaseFactory
import com.dating.home.domain.notification.PushNotificationService
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformChatDataModule = module {
    single { DatabaseFactory(androidContext()) }
    singleOf(::AppLifecycleObserver)
    singleOf(::ConnectivityObserver)
    singleOf(::ConnectionErrorHandler)

    singleOf(::FirebasePushNotificationService) bind PushNotificationService::class
}