package com.dv.apna.feature.splash.data.datasource

import com.dv.apna.core.firestore.FirestoreDataSource
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class SplashDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : FirestoreDataSource(firestore)