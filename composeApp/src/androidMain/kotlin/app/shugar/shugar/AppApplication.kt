package app.shugar.shugar

import android.app.Application
import app.shugar.shugar.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class AppApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@AppApplication)
            androidLogger()
        }
    }
}