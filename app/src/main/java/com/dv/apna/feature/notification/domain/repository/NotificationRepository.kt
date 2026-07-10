package com.dv.apna.feature.notification.domain.repository

import com.dv.apna.core.common.Resource
import kotlinx.coroutines.flow.Flow
import com.dv.apna.feature.notification.domain.model.NotificationModel

interface NotificationRepository {
    fun getNotifications(villageId: String): Flow<Resource<List<NotificationModel>>>
}