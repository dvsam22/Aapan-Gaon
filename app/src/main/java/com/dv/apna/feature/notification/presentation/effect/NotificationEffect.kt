package com.dv.apna.feature.notification.presentation.effect

sealed interface NotificationEffect {
    data class ShowSnackbar(val message: String) : NotificationEffect
}