package com.dv.apna.feature.home.data.datasource

import com.dv.apna.core.firestore.FirestoreDataSource
import com.dv.apna.feature.home.data.model.BannerDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class HomeDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : FirestoreDataSource(firestore) {
    
    suspend fun getBanners(villageId: String): List<BannerDto> {
        return getCollection("villages")
            .document(villageId)
            .collection("banners")
            .get()
            .await()
            .toObjects(BannerDto::class.java)
    }
}
