package com.dv.apna.feature.education.data.datasource

import com.dv.apna.core.firestore.FirestoreDataSource
import com.dv.apna.feature.education.data.model.EducationDto
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class EducationDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : FirestoreDataSource(firestore) {

    suspend fun getEducationData(villageId: String, categoryId: String): List<EducationDto> {
        return fetchBlockDocuments(
            locationId = villageId,
            subCollection = "education",
            categoryId = categoryId
        ).mapNotNull { doc ->
            doc.toObject(EducationDto::class.java)?.copy(id = doc.id)
        }
    }
}
