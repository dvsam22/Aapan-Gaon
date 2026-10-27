package com.dv.apna.core.notification.local

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.dv.apna.MainActivity
import com.dv.apna.R
import java.util.concurrent.atomic.AtomicBoolean

object LocalNotificationManager {

    // Using v2 to ensure Android applies IMPORTANCE_HIGH (heads-up banner)
    const val DAILY_CHANNEL_ID = "apna_local_daily_channel_v2"
    private const val DAILY_CHANNEL_NAME = "दैनिक सूचनाएं एवं सेवाएं"

    val isAppInForeground = AtomicBoolean(false)

    fun createNotificationChannel(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            ?: return

        val channel = NotificationChannel(
            DAILY_CHANNEL_ID,
            DAILY_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "गांव के मंडी भाव, खबरें, लेबर-मिस्त्री और फैमिली फंक्शन के दैनिक अपडेट्स"
            enableVibration(true)
            enableLights(true)
        }
        manager.createNotificationChannel(channel)
    }

    fun showNotification(context: Context, item: LocalNotificationItem) {
        // Do not interrupt the user if they are currently inside the app
        if (isAppInForeground.get()) {
            return
        }

        createNotificationChannel(context)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            ?: return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            if (item.targetType != "home" && item.targetType != "bounce") {
                putExtra("id", item.id.toString())
                putExtra("notification_id", item.id.toString())
            }
            putExtra("type", item.targetType)
            putExtra("notification_type", item.targetType)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            item.id,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, DAILY_CHANNEL_ID)
            .setSmallIcon(R.drawable.trasparent_logo)
            .setContentTitle(item.title)
            .setContentText(item.body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(item.body))
            .setColor(0xFF2CA074.toInt())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        manager.notify(item.id, notification)
    }
}
