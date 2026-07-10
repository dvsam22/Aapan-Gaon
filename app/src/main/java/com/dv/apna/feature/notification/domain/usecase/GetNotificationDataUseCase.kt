package com.dv.apna.feature.notification.domain.usecase

import com.dv.apna.feature.notification.domain.repository.NotificationRepository
import javax.inject.Inject

class GetNotificationDataUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    operator fun invoke(villageId: String) = repository.getNotifications(villageId)
}