package com.dv.apna.feature.notification.presentation.event

sealed interface NotificationEvent {
    data object Refresh : NotificationEvent
    data object BackClick : NotificationEvent
    data class SelectNotification(val notificationId: String) : NotificationEvent
}
