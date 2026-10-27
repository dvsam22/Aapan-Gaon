package com.dv.apna.core.notification.local

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

object LocalNotificationScheduler {

    private const val WORK_NAME_DAILY = "apna_daily_notification_work"

    /**
     * Calculates the exact initial delay until the next target slot (8:30 AM or 7:00 PM)
     * and enqueues a unique OneTimeWorkRequest.
     */
    fun scheduleNextDailyWorker(context: Context) {
        val now = Calendar.getInstance()

        val morningToday = (now.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, LocalNotificationSchedule.MORNING_HOUR)
            set(Calendar.MINUTE, LocalNotificationSchedule.MORNING_MINUTE)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val eveningToday = (now.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, LocalNotificationSchedule.EVENING_HOUR)
            set(Calendar.MINUTE, LocalNotificationSchedule.EVENING_MINUTE)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val targetCal = when {
            now.before(morningToday) -> morningToday
            now.before(eveningToday) -> eveningToday
            else -> {
                // Past evening, schedule for tomorrow morning
                (morningToday.clone() as Calendar).apply {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }
        }

        val delayMs = (targetCal.timeInMillis - now.timeInMillis).coerceAtLeast(1000L)

        val request = OneTimeWorkRequestBuilder<DailyNotificationWorker>()
            .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
            .addTag(WORK_NAME_DAILY)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            WORK_NAME_DAILY,
            ExistingWorkPolicy.REPLACE,
            request
        )
    }
}
