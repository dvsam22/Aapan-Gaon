package com.dv.apna.feature.transport.data.datasource

import com.dv.apna.core.firestore.FirestoreDataSource
import com.dv.apna.feature.transport.data.model.TransportDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TransportDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : FirestoreDataSource(firestore) {

    suspend fun getTransportData(villageId: String, categoryId: String): List<TransportDto> {
        return try {
            getCollection("villages")
                .document(villageId)
                .collection("transport")
                .whereEqualTo("categoryId", categoryId)
                .get()
                .await()
                .toObjects(TransportDto::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
