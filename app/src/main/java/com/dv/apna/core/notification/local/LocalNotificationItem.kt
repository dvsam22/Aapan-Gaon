package com.dv.apna.core.notification.local

data class LocalNotificationItem(
    val id: Int,
    val title: String,
    val body: String,
    val targetType: String,
    val targetId: String = ""
)
