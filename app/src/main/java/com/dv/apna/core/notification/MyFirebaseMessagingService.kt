package com.dv.apna.core.notification

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

        remoteMessage.notification?.let {
            val title = it.title ?: "Apna"
            val body = it.body ?: ""
            val id = remoteMessage.data["id"] ?: remoteMessage.data["notification_id"]
            val type = remoteMessage.data["type"] ?: remoteMessage.data["notification_type"]
            notificationHelper.showNotification(title, body, id, type)
        } ?: run {
            // Handle data payload if notification is null
            if (remoteMessage.data.isNotEmpty()) {
                val title = remoteMessage.data["title"] ?: "Apna"
                val message = remoteMessage.data["message"] ?: ""
                val id = remoteMessage.data["id"] ?: remoteMessage.data["notification_id"]
                val type = remoteMessage.data["type"] ?: remoteMessage.data["notification_type"]
                notificationHelper.showNotification(title, message, id, type)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}
