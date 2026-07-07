package com.dv.apna.feature.health.data.datasource

import com.dv.apna.core.firestore.FirestoreDataSource
import com.dv.apna.feature.health.data.model.HealthDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class HealthDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : FirestoreDataSource(firestore) {

    suspend fun getHealthData(villageId: String, categoryId: String): List<HealthDto> {
        return try {
            getCollection("villages")
                .document(villageId)
                .collection("health")
                .whereEqualTo("categoryId", categoryId)
                .get()
                .await()
                .toObjects(HealthDto::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
