package com.dv.apna.feature.health.data.datasource

import com.dv.apna.core.firestore.FirestoreDataSource
import com.dv.apna.feature.health.data.model.HealthDto
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class HealthDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : FirestoreDataSource(firestore) {

    suspend fun getHealthData(villageId: String, categoryId: String): List<HealthDto> {
        return fetchBlockDocuments(
            locationId = villageId,
            subCollection = "health",
            categoryId = categoryId
        ).mapNotNull { doc ->
            doc.toObject(HealthDto::class.java)?.copy(id = doc.id)
        }
    }
}
