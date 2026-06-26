package com.dv.apna.feature.settings.data.datasource

import com.dv.apna.core.firestore.FirestoreDataSource
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class SettingsDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : FirestoreDataSource(firestore)