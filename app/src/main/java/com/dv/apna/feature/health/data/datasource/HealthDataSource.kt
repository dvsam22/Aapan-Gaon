package com.dv.apna.feature.health.data.datasource

import com.dv.apna.core.firestore.FirestoreDataSource
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class HealthDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : FirestoreDataSource(firestore)