package com.dating.aura

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.gif.AnimatedImageDecoder
import coil3.request.CachePolicy
import com.dating.aura.di.initKoin
import com.dating.composeapp.BuildKonfig
import com.dating.home.data.inactivity.InactivityNotificationScheduler
import com.google.android.libraries.places.api.Places
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class AppApplication: Application(), SingletonImageLoader.Factory {

    override fun onCreate() {
        super.onCreate()
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, BuildKonfig.PLACES_API_KEY)
        }
        initKoin {
            androidContext(this@AppApplication)
            androidLogger()
        }
        setupInactivityNotification()
    }

    private fun setupInactivityNotification() {
        val scheduler = get<InactivityNotificationScheduler>()
        ProcessLifecycleOwner.get().lifecycle.addObserver(
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_START) {
                    scheduler.schedule()
                }
            }
        )
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun newImageLoader(context: coil3.PlatformContext): ImageLoader {
        return ImageLoader.Builder(context)
            .components {
                add(AnimatedImageDecoder.Factory())
            }
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .build()
    }
}