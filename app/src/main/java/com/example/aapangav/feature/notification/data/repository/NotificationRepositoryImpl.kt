package com.example.aapangav.feature.notification.data.repository

import com.example.aapangav.core.common.Resource
import com.example.aapangav.feature.notification.domain.model.NotificationModel
import com.example.aapangav.feature.notification.domain.repository.NotificationRepository
import com.example.aapangav.feature.notification.data.datasource.NotificationDataSource
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