package com.dv.apna.feature.mandi.data.datasource

import com.dv.apna.core.firestore.FirestoreDataSource
import com.dv.apna.feature.mandi.data.model.MandiDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MandiDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : FirestoreDataSource(firestore) {

    suspend fun getMandiData(villageId: String, categoryId: String): List<MandiDto> {
        return try {
            getCollection("villages")
                .document(villageId)
                .collection("mandi")
                .whereEqualTo("categoryId", categoryId)
                .get()
                .await()
                .toObjects(MandiDto::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
