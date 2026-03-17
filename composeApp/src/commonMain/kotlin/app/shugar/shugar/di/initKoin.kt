package app.shugar.shugar.di

import com.dating.auth.presentation.di.authPresentationModule
import com.dating.home.data.di.homeDataModule
import com.dating.home.presentation.di.homePresentationModule
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
            homePresentationModule,
            corePresentationModule,
            homeDataModule
        )
    }
}