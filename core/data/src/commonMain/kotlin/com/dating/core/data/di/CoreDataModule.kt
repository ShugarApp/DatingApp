package com.dating.core.data.di

import com.dating.core.data.auth.DataStoreSessionStorage
import com.dating.core.data.auth.KtorAuthService
import com.dating.core.data.logging.KermitLogger
import com.dating.core.data.networking.HttpClientFactory
import com.dating.core.domain.auth.AuthService
import com.dating.core.domain.auth.SessionStorage
import com.dating.core.domain.logging.AppLogger
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformCoreDataModule: Module

val coreDataModule = module {
    includes(platformCoreDataModule)
    single<AppLogger> { KermitLogger }
    single {
        HttpClientFactory(get(), get()).create(get())
    }
    singleOf(::KtorAuthService) bind AuthService::class
    singleOf(::DataStoreSessionStorage) bind SessionStorage::class
}