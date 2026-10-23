package com.dv.apna.feature.mandi.data.datasource

import com.dv.apna.core.firestore.FirestoreDataSource
import com.dv.apna.feature.mandi.data.model.MandiDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MandiDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : FirestoreDataSource(firestore) {

    suspend fun getMandiData(villageId: String, categoryId: String): List<MandiDto> {
        val villageData = try {
            getCollection("villages")
                .document(villageId)
                .collection("mandi")
                .whereEqualTo("categoryId", categoryId)
                .get()
                .await()
                .documents.mapNotNull { doc ->
                    doc.toObject(MandiDto::class.java)?.copy(id = doc.id)
                }
        } catch (e: Exception) {
            emptyList()
        }

        val centralData = try {
            getCollection("mandi")
                .whereEqualTo("categoryId", categoryId)
                .get()
                .await()
                .documents.mapNotNull { doc ->
                    doc.toObject(MandiDto::class.java)?.copy(id = doc.id)
                }
        } catch (e: Exception) {
            emptyList()
        }

        // Merge village-level custom entries with central daily mandi entries
        val combinedMap = linkedMapOf<String, MandiDto>()
        villageData.forEach { item ->
            val id = item.id.ifBlank { null } ?: return@forEach
            combinedMap[id] = item
        }
        centralData.forEach { item ->
            val id = item.id.ifBlank { null } ?: return@forEach
            if (!combinedMap.containsKey(id)) {
                combinedMap[id] = item
            }
        }

        return combinedMap.values.sortedByDescending { it.date }
    }
}
