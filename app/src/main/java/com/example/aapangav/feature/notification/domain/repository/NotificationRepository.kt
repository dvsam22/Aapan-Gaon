package com.example.aapangav.feature.notification.domain.repository

import com.example.aapangav.core.common.Resource
import kotlinx.coroutines.flow.Flow
import com.example.aapangav.feature.notification.domain.model.NotificationModel

interface NotificationRepository {
    fun getData(): Flow<Resource<List<NotificationModel>>>
}