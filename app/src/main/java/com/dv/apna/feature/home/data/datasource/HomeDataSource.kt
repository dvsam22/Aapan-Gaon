package com.dv.apna.feature.home.data.datasource

import com.dv.apna.core.firestore.FirestoreDataSource
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class HomeDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : FirestoreDataSource(firestore) {
    // Feature specific Firestore calls
}
