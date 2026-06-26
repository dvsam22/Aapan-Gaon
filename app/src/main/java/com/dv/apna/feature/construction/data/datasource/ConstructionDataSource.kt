package com.dv.apna.feature.construction.data.datasource

import com.dv.apna.core.firestore.FirestoreDataSource
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class ConstructionDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : FirestoreDataSource(firestore)