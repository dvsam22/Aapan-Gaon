package com.example.aapangav.feature.mandi.data.datasource

import com.example.aapangav.core.firestore.FirestoreDataSource
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class MandiDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : FirestoreDataSource(firestore)