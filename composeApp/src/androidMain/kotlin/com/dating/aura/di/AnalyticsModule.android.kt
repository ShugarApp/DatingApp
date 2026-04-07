package com.dating.aura.di

import com.dating.aura.analytics.AnalyticsService
import com.dating.aura.analytics.FirebaseAnalyticsService
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformAnalyticsModule = module {
    single { FirebaseAnalyticsService(androidContext()) } bind AnalyticsService::class
}
