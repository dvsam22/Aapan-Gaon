package com.dv.apna.feature.family.data.datasource

import com.dv.apna.core.firestore.FirestoreDataSource
import com.dv.apna.feature.family.data.model.FamilyFunctionDto
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class FamilyFunctionDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : FirestoreDataSource(firestore) {

    suspend fun getFamilyFunctions(villageId: String, categoryId: String): List<FamilyFunctionDto> {
        return fetchBlockDocuments(
            locationId = villageId,
            subCollection = "family_functions",
            categoryId = categoryId
        ).mapNotNull { doc ->
            doc.toObject(FamilyFunctionDto::class.java)?.copy(id = doc.id)
        }
    }
}
