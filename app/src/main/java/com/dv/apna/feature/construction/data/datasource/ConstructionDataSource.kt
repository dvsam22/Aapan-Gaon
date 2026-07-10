package com.dv.apna.feature.construction.data.datasource

import com.dv.apna.core.firestore.FirestoreDataSource
import com.dv.apna.feature.construction.data.model.ConstructionDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ConstructionDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : FirestoreDataSource(firestore) {

    suspend fun getConstructionData(villageId: String, categoryId: String): List<ConstructionDto> {
        return getCollection("villages")
            .document(villageId)
            .collection("construction")
            .whereEqualTo("categoryId", categoryId)
            .get()
            .await()
            .toObjects(ConstructionDto::class.java)
    }
}
