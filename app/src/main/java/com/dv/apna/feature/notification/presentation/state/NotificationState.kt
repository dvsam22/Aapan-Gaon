package com.dv.apna.feature.notification.presentation.state

import com.dv.apna.core.common.UiText
import com.dv.apna.feature.notification.domain.model.NotificationModel

data class NotificationState(
    val isLoading: Boolean = true,
    val notifications: List<NotificationModel> = emptyList(),
    val error: UiText? = null
)
