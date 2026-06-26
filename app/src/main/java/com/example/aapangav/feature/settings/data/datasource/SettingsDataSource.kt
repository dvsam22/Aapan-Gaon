package com.example.aapangav.feature.settings.data.datasource

import com.example.aapangav.core.firestore.FirestoreDataSource
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class SettingsDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : FirestoreDataSource(firestore)