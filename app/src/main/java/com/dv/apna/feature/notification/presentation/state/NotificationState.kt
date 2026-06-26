package com.dv.apna.feature.notification.presentation.state

data class NotificationState(
    val isLoading: Boolean = false,
    val error: String? = null
)