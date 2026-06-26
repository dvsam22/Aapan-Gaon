package com.example.aapangav.feature.language.data.datasource

import com.example.aapangav.core.firestore.FirestoreDataSource
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class LanguageDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : FirestoreDataSource(firestore)