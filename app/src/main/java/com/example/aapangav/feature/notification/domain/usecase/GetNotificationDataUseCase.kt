package com.example.aapangav.feature.notification.domain.usecase

import com.example.aapangav.feature.notification.domain.repository.NotificationRepository
import javax.inject.Inject

class GetNotificationDataUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    operator fun invoke() = repository.getData()
}