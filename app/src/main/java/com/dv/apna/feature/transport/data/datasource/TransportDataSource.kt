package com.dv.apna.feature.transport.data.datasource

import com.dv.apna.core.firestore.FirestoreDataSource
import com.dv.apna.feature.transport.data.model.TransportDto
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class TransportDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : FirestoreDataSource(firestore) {

    suspend fun getTransportData(villageId: String, categoryId: String): List<TransportDto> {
        return fetchBlockDocuments(
            locationId = villageId,
            subCollection = "transport",
            categoryId = categoryId
        ).mapNotNull { doc ->
            doc.toObject(TransportDto::class.java)?.copy(id = doc.id)
        }
    }
}
