package com.dv.apna.core.notification

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationHelper: NotificationHelper

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCM", "From: ${remoteMessage.from}")

        remoteMessage.notification?.let {
            Log.d("FCM", "Notification Message Body: ${it.body}")
            Log.d("FCM", "Notification Message Body: ${it}")
            val title = it.title ?: "Apna"
            val body = it.body ?: ""
            val id = remoteMessage.data["id"] ?: remoteMessage.data["notification_id"]
            val type = remoteMessage.data["type"] ?: remoteMessage.data["notification_type"]
            notificationHelper.showNotification(title, body, id, type)
        } ?: run {
            Log.d("FCM", "Notification payload is null, checking data payload")
            // Handle data payload if notification is null
            if (remoteMessage.data.isNotEmpty()) {
                Log.d("FCM", "Message data payload: " + remoteMessage.data)
                val title = remoteMessage.data["title"] ?: "Apna"
                val message = remoteMessage.data["message"] ?: ""
                val id = remoteMessage.data["id"] ?: remoteMessage.data["notification_id"]
                val type = remoteMessage.data["type"] ?: remoteMessage.data["notification_type"]
                notificationHelper.showNotification(title, message, id, type)
            } else {
                Log.d("FCM", "No notification or data payload found")
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Refreshed token: $token")
        // You might want to send this token to your server
    }
}
