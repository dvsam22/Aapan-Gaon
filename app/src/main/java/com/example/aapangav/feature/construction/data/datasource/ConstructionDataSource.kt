package com.example.aapangav.feature.construction.data.datasource

import com.example.aapangav.core.firestore.FirestoreDataSource
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class ConstructionDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : FirestoreDataSource(firestore)