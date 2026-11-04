package com.dv.apna.feature.legal_panchayat.data.datasource

import com.dv.apna.core.firestore.FirestoreDataSource
import com.dv.apna.feature.legal_panchayat.data.model.LegalPanchayatDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LegalPanchayatDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : FirestoreDataSource(firestore) {

    suspend fun getLegalPanchayatData(villageId: String, categoryId: String): List<LegalPanchayatDto> {
        val firestoreResults = try {
            val villageResults = if (villageId.isNotBlank()) {
                getCollection("villages")
                    .document(villageId)
                    .collection("legal_panchayat")
                    .whereEqualTo("categoryId", categoryId)
                    .get()
                    .await()
                    .toObjects(LegalPanchayatDto::class.java)
            } else emptyList()

            if (villageResults.isNotEmpty()) {
                villageResults
            } else {
                getCollection("legal_panchayat")
                    .whereEqualTo("categoryId", categoryId)
                    .get()
                    .await()
                    .toObjects(LegalPanchayatDto::class.java)
            }
        } catch (e: Exception) {
            emptyList()
        }

        return firestoreResults
    }
}
