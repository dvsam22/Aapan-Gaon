package com.dv.apna.feature.notification.data.datasource

import com.dv.apna.core.firestore.FirestoreDataSource
import com.dv.apna.feature.notification.data.model.NotificationDto
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class NotificationDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : FirestoreDataSource(firestore) {

    suspend fun getNotifications(villageId: String): List<NotificationDto> {
        return fetchBlockDocuments(locationId = villageId, subCollection = "notifications")
            .mapNotNull { doc ->
                doc.toObject(NotificationDto::class.java)?.apply { id = doc.id }
            }
            .filter { !it.type.equals("news", ignoreCase = true) }
            .sortedByDescending { it.date }
    }
}
