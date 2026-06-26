package com.dv.apna.feature.mandi.data.datasource

import com.dv.apna.core.firestore.FirestoreDataSource
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class MandiDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : FirestoreDataSource(firestore)