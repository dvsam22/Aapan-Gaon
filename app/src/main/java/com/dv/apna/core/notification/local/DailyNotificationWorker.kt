package com.dv.apna.core.notification.local

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import java.util.Calendar

class DailyNotificationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val now = Calendar.getInstance()
            val slot = LocalNotificationSchedule.getCurrentSlot(now)
            val dayOfWeek = now.get(Calendar.DAY_OF_WEEK)

            val item = LocalNotificationSchedule.getNotificationFor(dayOfWeek, slot)
            LocalNotificationManager.showNotification(applicationContext, item)

            // Reschedule next morning or evening execution
            LocalNotificationScheduler.scheduleNextDailyWorker(applicationContext)

            Result.success()
        } catch (e: Exception) {
            // Even on error, ensure future schedules remain active
            LocalNotificationScheduler.scheduleNextDailyWorker(applicationContext)
            Result.failure()
        }
    }
}
