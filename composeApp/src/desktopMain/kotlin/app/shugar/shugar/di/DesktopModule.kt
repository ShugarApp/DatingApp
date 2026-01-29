package app.shugar.shugar.di

import app.shugar.shugar.ApplicationStateHolder
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val desktopModule = module {
    singleOf(::ApplicationStateHolder)
}