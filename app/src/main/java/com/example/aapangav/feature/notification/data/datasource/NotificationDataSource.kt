package com.example.aapangav.feature.notification.data.datasource

import com.example.aapangav.core.firestore.FirestoreDataSource
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class NotificationDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : FirestoreDataSource(firestore)