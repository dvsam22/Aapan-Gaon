package com.example.aapangav.feature.news.data.datasource

import com.example.aapangav.core.firestore.FirestoreDataSource
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class NewsDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : FirestoreDataSource(firestore)