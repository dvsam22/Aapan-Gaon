package com.example.aapangav.feature.notification.presentation.effect

sealed interface NotificationEffect {
    data class ShowSnackbar(val message: String) : NotificationEffect
}