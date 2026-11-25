package com.dv.apna.feature.labour.data.datasource

import com.dv.apna.core.firestore.FirestoreDataSource
import com.dv.apna.feature.labour.data.model.LabourDto
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class LabourDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : FirestoreDataSource(firestore) {

    suspend fun getLabours(villageId: String, categoryId: String): List<LabourDto> {
        return fetchBlockDocuments(
            locationId = villageId,
            subCollection = "labour",
            categoryId = categoryId
        ).mapNotNull { doc ->
            doc.toObject(LabourDto::class.java)?.copy(id = doc.id)
        }
    }
}
