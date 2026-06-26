package com.example.aapangav.core.firestore

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Base class for all Firestore data sources.
 * Encapsulates common Firestore operations and provides a centralized access point.
 */
abstract class FirestoreDataSource(
    protected val firestore: FirebaseFirestore
) {
    protected fun getCollection(path: String): CollectionReference {
        return firestore.collection(path)
    }

    protected fun getDocument(path: String): DocumentReference {
        return firestore.document(path)
    }
}