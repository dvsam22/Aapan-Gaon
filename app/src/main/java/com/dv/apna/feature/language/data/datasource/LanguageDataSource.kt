package com.dv.apna.feature.language.data.datasource

import com.dv.apna.core.firestore.FirestoreDataSource
import com.dv.apna.feature.language.data.model.VillageDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LanguageDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : FirestoreDataSource(firestore) {

    suspend fun getVillages(): List<VillageDto> {
        return getCollection("villages")
            .get()
            .await()
            .toObjects(VillageDto::class.java)
    }
}
