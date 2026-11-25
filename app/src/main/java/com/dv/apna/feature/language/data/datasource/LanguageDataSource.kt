package com.dv.apna.feature.language.data.datasource

import com.dv.apna.core.firestore.FirestoreDataSource
import com.dv.apna.feature.language.data.model.VillageDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LanguageDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : FirestoreDataSource(firestore) {

    suspend fun getBlocks(): List<VillageDto> {
        return try {
            val snapshot = getCollection(BLOCKS_COLLECTION)
                .get()
                .await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(VillageDto::class.java)?.copy(id = doc.id)
            }.sortedBy { it.order ?: 99 }
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun getVillages(): List<VillageDto> {
        val snapshot = getCollection(VILLAGES_COLLECTION)
            .get()
            .await()
        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(VillageDto::class.java)?.copy(id = doc.id)
        }
    }
}
