package com.dv.apna.feature.business.data.datasource

import com.dv.apna.core.firestore.FirestoreDataSource
import com.dv.apna.feature.business.data.model.BusinessRegistrationDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class BusinessDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : FirestoreDataSource(firestore) {

    suspend fun submitRegistration(dto: BusinessRegistrationDto) {
        getCollection("business_registrations")
            .add(dto.toMap())
            .await()
    }
}
