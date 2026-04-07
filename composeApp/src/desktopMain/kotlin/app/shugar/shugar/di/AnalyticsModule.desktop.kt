package com.dating.aura.di

import com.dating.aura.analytics.AnalyticsService
import com.dating.aura.analytics.NoOpAnalyticsService
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformAnalyticsModule = module {
    single { NoOpAnalyticsService() } bind AnalyticsService::class
}
