package com.dv.apna.feature.notification.data.repository

import com.dv.apna.core.common.Resource
import com.dv.apna.feature.notification.domain.model.NotificationModel
import com.dv.apna.feature.notification.domain.repository.NotificationRepository
import com.dv.apna.feature.notification.data.datasource.NotificationDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val dataSource: NotificationDataSource
) : NotificationRepository {
    override fun getData(): Flow<Resource<List<NotificationModel>>> = flow {
        emit(Resource.Loading())
    }
}