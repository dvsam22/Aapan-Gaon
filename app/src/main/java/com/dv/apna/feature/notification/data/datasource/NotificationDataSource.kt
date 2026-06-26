package com.dv.apna.feature.notification.data.datasource

import com.dv.apna.core.firestore.FirestoreDataSource
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class NotificationDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : FirestoreDataSource(firestore)