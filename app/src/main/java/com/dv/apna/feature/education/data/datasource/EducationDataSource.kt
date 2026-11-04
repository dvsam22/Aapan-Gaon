package com.dv.apna.feature.education.data.datasource

import com.dv.apna.core.firestore.FirestoreDataSource
import com.dv.apna.feature.education.data.model.EducationDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class EducationDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : FirestoreDataSource(firestore) {

    suspend fun getEducationData(villageId: String, categoryId: String): List<EducationDto> {
        val firestoreResults = try {
            val villageResults = if (villageId.isNotBlank()) {
                getCollection("villages")
                    .document(villageId)
                    .collection("education")
                    .whereEqualTo("categoryId", categoryId)
                    .get()
                    .await()
                    .toObjects(EducationDto::class.java)
            } else emptyList()

            if (villageResults.isNotEmpty()) {
                villageResults
            } else {
                getCollection("education")
                    .whereEqualTo("categoryId", categoryId)
                    .get()
                    .await()
                    .toObjects(EducationDto::class.java)
            }
        } catch (e: Exception) {
            emptyList()
        }

        return firestoreResults
    }
}
