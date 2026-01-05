package com.dating.aura.di

import com.dating.auth.presentation.di.authPresentationModule
import com.dating.chat.data.di.chatDataModule
import com.dating.chat.presentation.di.chatPresentationModule
import com.dating.core.data.di.coreDataModule
import com.dating.core.presentation.di.corePresentationModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(
            coreDataModule,
            authPresentationModule,
            appModule,
            chatPresentationModule,
            corePresentationModule,
            chatDataModule
        )
    }
}