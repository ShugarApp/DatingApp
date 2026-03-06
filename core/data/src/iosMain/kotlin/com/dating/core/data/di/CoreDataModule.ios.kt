package com.dating.core.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.dating.core.data.auth.createDataStore
import com.dating.core.data.location.IosLocationProvider
import com.dating.core.domain.location.LocationProvider
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import org.koin.dsl.module

actual val platformCoreDataModule = module {
    single<HttpClientEngine> { Darwin.create() }
    single<DataStore<Preferences>> {
        createDataStore()
    }
    single<LocationProvider> { IosLocationProvider() }
}
