package com.dating.core.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.dating.core.data.auth.createDataStore
import com.dating.core.data.location.AndroidLocationProvider
import com.dating.core.domain.location.LocationProvider
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformCoreDataModule = module {
    single<HttpClientEngine> { OkHttp.create() }
    single<DataStore<Preferences>> {
        createDataStore(androidContext())
    }
    single<LocationProvider> { AndroidLocationProvider(androidContext()) }
}
