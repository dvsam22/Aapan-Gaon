package com.dv.apna.feature.news.data.datasource

import com.dv.apna.core.firestore.FirestoreDataSource
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class NewsDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : FirestoreDataSource(firestore)