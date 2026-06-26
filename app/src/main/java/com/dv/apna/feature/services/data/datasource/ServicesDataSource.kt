package com.dv.apna.feature.services.data.datasource

import com.dv.apna.core.firestore.FirestoreDataSource
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class ServicesDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : FirestoreDataSource(firestore)