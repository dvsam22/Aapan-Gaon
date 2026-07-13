package com.dv.apna.feature.family.data.datasource

import com.dv.apna.core.firestore.FirestoreDataSource
import com.dv.apna.feature.family.data.model.FamilyFunctionDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FamilyFunctionDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : FirestoreDataSource(firestore) {

    suspend fun getFamilyFunctions(villageId: String, categoryId: String): List<FamilyFunctionDto> {
        return getCollection("villages")
            .document(villageId)
            .collection("family_functions")
            .whereEqualTo("categoryId", categoryId)
            .get()
            .await()
            .toObjects(FamilyFunctionDto::class.java)
    }
}
