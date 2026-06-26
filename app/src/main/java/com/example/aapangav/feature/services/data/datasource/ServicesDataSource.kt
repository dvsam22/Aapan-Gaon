package com.example.aapangav.feature.services.data.datasource

import com.example.aapangav.core.firestore.FirestoreDataSource
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class ServicesDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : FirestoreDataSource(firestore)