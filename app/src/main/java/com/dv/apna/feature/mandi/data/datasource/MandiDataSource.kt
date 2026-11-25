package com.dv.apna.feature.mandi.data.datasource

import com.dv.apna.core.firestore.FirestoreDataSource
import com.dv.apna.feature.mandi.data.model.MandiDto
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class MandiDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : FirestoreDataSource(firestore) {

    suspend fun getMandiData(villageId: String, categoryId: String): List<MandiDto> {
        return fetchBlockDocuments(
            locationId = villageId,
            subCollection = "mandi",
            categoryId = categoryId
        ).mapNotNull { doc ->
            doc.toObject(MandiDto::class.java)?.copy(id = doc.id)
        }.sortedByDescending { it.date }
    }

    suspend fun getAllMandiPrices(villageId: String): List<MandiDto> {
        return fetchBlockDocuments(
            locationId = villageId,
            subCollection = "mandi"
        ).mapNotNull { doc ->
            doc.toObject(MandiDto::class.java)?.copy(id = doc.id)
        }
            .filter { it.categoryId != "buyers" }
            .sortedByDescending { it.date }
            .distinctBy { item -> "${item.cropName}_${item.market}".ifBlank { item.id } }
    }
}
