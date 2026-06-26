package com.example.aapangav.feature.splash.data.datasource

import com.example.aapangav.core.firestore.FirestoreDataSource
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class SplashDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : FirestoreDataSource(firestore)