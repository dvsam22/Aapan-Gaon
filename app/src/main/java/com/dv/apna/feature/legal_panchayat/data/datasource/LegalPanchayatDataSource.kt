package com.dv.apna.feature.legal_panchayat.data.datasource

import com.dv.apna.core.firestore.FirestoreDataSource
import com.dv.apna.feature.legal_panchayat.data.model.LegalPanchayatDto
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class LegalPanchayatDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : FirestoreDataSource(firestore) {

    suspend fun getLegalPanchayatData(villageId: String, categoryId: String): List<LegalPanchayatDto> {
        return fetchBlockDocuments(
            locationId = villageId,
            subCollection = "legal_panchayat",
            categoryId = categoryId
        ).mapNotNull { doc ->
            doc.toObject(LegalPanchayatDto::class.java)?.copy(id = doc.id)
        }
    }
}
