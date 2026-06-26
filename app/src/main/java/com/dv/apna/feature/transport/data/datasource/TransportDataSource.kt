package com.dv.apna.feature.transport.data.datasource

import com.dv.apna.core.firestore.FirestoreDataSource
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class TransportDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : FirestoreDataSource(firestore)