package com.dating.core.data.di

import com.dating.core.data.auth.createDataStore
import com.dating.core.data.location.DesktopLocationProvider
import com.dating.core.domain.location.LocationProvider
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.dsl.module

actual val platformCoreDataModule = module {
    single { createDataStore() }
    single<HttpClientEngine> { OkHttp.create() }
    single<LocationProvider> { DesktopLocationProvider() }
}
