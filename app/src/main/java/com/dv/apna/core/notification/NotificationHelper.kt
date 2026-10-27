package com.dv.apna.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.dv.apna.MainActivity
import com.dv.apna.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        private const val CHANNEL_ID = "apna_notification_channel"
        private const val CHANNEL_NAME = "Apna Notifications"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Used for Apna app notifications"
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun cleanNewsNotificationText(text: String): String {
        if (text.isBlank()) return text
        return text
            .replace(Regex("(?i)maharajganj\\s*news\\s*[:\\-\\|]\\s*"), "")
            .replace(Regex("(?i)maharajganj\\s*[:\\-\\|]\\s*"), "")
            .replace(Regex("महराजगंज\\s*(न्यूज़|समाचार)?\\s*[:\\-\\|]\\s*"), "")
            .replace(Regex("(?i)read\\s+(latest\\s+)?maharajganj\\s+news\\s+(today\\s+)?in\\s+hindi\\s*[-—–]?\\s*"), "")
            .replace(Regex("(?i)read\\s+latest\\s+news\\s+in\\s+hindi\\s*[-—–]?\\s*"), "")
            .replace(Regex("^(महराजगंज|महाराजगंज)\\s*[।|:\\-—–]\\s*"), "")
            .trim()
    }

    fun showNotification(title: String, message: String, notificationId: String? = null, type: String? = null) {
        val cleanTitle = cleanNewsNotificationText(title)
        val cleanMessage = cleanNewsNotificationText(message)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            if (!notificationId.isNullOrEmpty()) {
                putExtra("id", notificationId)
                putExtra("notification_id", notificationId)
            }
            if (!type.isNullOrEmpty()) {
                putExtra("type", type)
                putExtra("notification_type", type)
            }
        }
        
        val requestCode = (System.currentTimeMillis() and 0xfffffff).toInt()
        val pendingIntent = PendingIntent.getActivity(
            context, requestCode, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.trasparent_logo)
            .setContentTitle(cleanTitle)
            .setContentText(cleanMessage)
            .setStyle(NotificationCompat.BigTextStyle().bigText(cleanMessage))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(requestCode, notification)
    }
}
