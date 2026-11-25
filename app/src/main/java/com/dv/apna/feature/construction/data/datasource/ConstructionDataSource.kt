package com.dv.apna.feature.construction.data.datasource

import com.dv.apna.core.firestore.FirestoreDataSource
import com.dv.apna.feature.construction.data.model.ConstructionDto
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class ConstructionDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : FirestoreDataSource(firestore) {

    suspend fun getConstructionData(villageId: String, categoryId: String): List<ConstructionDto> {
        return fetchBlockDocuments(
            locationId = villageId,
            subCollection = "construction",
            categoryId = categoryId
        ).mapNotNull { doc ->
            doc.toObject(ConstructionDto::class.java)?.copy(id = doc.id)
        }
    }
}
