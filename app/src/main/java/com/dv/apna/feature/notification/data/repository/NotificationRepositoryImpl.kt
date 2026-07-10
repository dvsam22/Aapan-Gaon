package com.dv.apna.feature.notification.data.repository

import com.dv.apna.core.common.Resource
import com.dv.apna.core.datastore.PreferenceManager
import com.dv.apna.feature.notification.data.datasource.NotificationDataSource
import com.dv.apna.feature.notification.data.mapper.toDomain
import com.dv.apna.feature.notification.domain.model.NotificationModel
import com.dv.apna.feature.notification.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val dataSource: NotificationDataSource,
    private val preferenceManager: PreferenceManager
) : NotificationRepository {
    override fun getNotifications(villageId: String): Flow<Resource<List<NotificationModel>>> = flow {
        emit(Resource.Loading())
        try {
            val languageCode = preferenceManager.languageCode.firstOrNull() ?: "en"
            val notifications = dataSource.getNotifications(villageId).map { it.toDomain(languageCode) }
            emit(Resource.Success(notifications))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown Error"))
        }
    }
}
