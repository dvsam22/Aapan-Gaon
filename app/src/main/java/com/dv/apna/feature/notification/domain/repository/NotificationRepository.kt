package com.dv.apna.feature.notification.domain.repository

import com.dv.apna.core.common.Resource
import kotlinx.coroutines.flow.Flow
import com.dv.apna.feature.notification.domain.model.NotificationModel

interface NotificationRepository {
    fun getData(): Flow<Resource<List<NotificationModel>>>
}