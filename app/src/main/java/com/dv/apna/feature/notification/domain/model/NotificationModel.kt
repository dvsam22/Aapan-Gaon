package com.dv.apna.feature.notification.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class NotificationModel(
    val id: String,
    val title: String,
    val summary: String,
    val description: String,
    val time: String,
    val date: String,
    val category: String // Today, Yesterday, etc.
)
