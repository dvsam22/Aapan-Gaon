package com.dv.apna.feature.notification.presentation.effect

sealed interface NotificationEffect {
    data object NavigateBack : NotificationEffect
    data class NavigateToDetails(val notificationId: String) : NotificationEffect
}
