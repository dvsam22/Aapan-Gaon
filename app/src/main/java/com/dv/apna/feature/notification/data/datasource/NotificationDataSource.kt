package com.dv.apna.feature.notification.data.datasource

import com.dv.apna.core.firestore.FirestoreDataSource
import com.dv.apna.feature.notification.data.model.NotificationDto
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NotificationDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : FirestoreDataSource(firestore) {

    suspend fun getNotifications(villageId: String): List<NotificationDto> {
        return getCollection("villages")
            .document(villageId)
            .collection("notifications")
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .await()
            .documents.mapNotNull { doc ->
                doc.toObject(NotificationDto::class.java)?.apply { id = doc.id }
            }
    }
}
