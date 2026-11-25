package com.dv.apna.core.firestore

import com.dv.apna.core.datastore.PreferenceManager
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Base class for all Firestore data sources.
 * Encapsulates common Firestore operations and provides a centralized access point.
 */
abstract class FirestoreDataSource(
    protected val firestore: FirebaseFirestore
) {
    companion object {
        const val DEFAULT_BLOCK_ID = PreferenceManager.DEFAULT_BLOCK_ID
        const val BLOCKS_COLLECTION = "blocks"
        const val VILLAGES_COLLECTION = "villages"
    }

    protected fun getCollection(path: String): CollectionReference {
        return firestore.collection(path)
    }

    protected fun getDocument(path: String): DocumentReference {
        return firestore.document(path)
    }

    protected fun resolveBlockId(savedLocationId: String?): String {
        return PreferenceManager.resolveBlockId(savedLocationId)
    }

    /**
     * Reads documents directly from `/blocks/{blockId}/{subCollection}`
     * (filtered by `categoryId` when provided).
     */
    protected suspend fun fetchBlockDocuments(
        locationId: String?,
        subCollection: String,
        categoryId: String? = null
    ): List<DocumentSnapshot> {
        val blockId = resolveBlockId(locationId)
        return try {
            val colRef = getCollection(BLOCKS_COLLECTION)
                .document(blockId)
                .collection(subCollection)
            val query = if (!categoryId.isNullOrBlank()) {
                colRef.whereEqualTo("categoryId", categoryId)
            } else {
                colRef
            }
            query.get().await().documents
        } catch (_: Exception) {
            emptyList()
        }
    }
}