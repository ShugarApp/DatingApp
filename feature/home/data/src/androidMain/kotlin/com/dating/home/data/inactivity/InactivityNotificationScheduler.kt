package com.dating.home.data.inactivity

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class InactivityNotificationScheduler(private val context: Context) {

    fun schedule() {
        val request = OneTimeWorkRequestBuilder<InactivityCheckWorker>()
            .setInitialDelay(INACTIVITY_DAYS, TimeUnit.DAYS)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    companion object {
        const val INACTIVITY_DAYS = 5L
        const val WORK_NAME = "inactivity_notification_work"
    }
}
