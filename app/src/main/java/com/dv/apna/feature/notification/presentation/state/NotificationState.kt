package com.dv.apna.feature.notification.presentation.state

import com.dv.apna.feature.notification.domain.model.NotificationModel

data class NotificationState(
    val isLoading: Boolean = false,
    val notifications: List<NotificationModel> = emptyList(),
    val error: String? = null
)
