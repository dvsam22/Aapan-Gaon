package com.dv.apna.feature.labour.data.datasource

import com.dv.apna.core.firestore.FirestoreDataSource
import com.dv.apna.feature.labour.data.model.LabourDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LabourDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : FirestoreDataSource(firestore) {

    suspend fun getLabours(villageId: String, categoryId: String): List<LabourDto> {
        return getCollection("villages")
            .document(villageId)
            .collection("labour")
            .whereEqualTo("categoryId", categoryId)
            .get()
            .await()
            .toObjects(LabourDto::class.java)
    }
}
