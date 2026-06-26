package com.example.aapangav.feature.health.data.datasource

import com.example.aapangav.core.firestore.FirestoreDataSource
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class HealthDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : FirestoreDataSource(firestore)