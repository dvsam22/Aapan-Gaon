package com.dv.apna.feature.home.data.datasource

import com.dv.apna.core.firestore.FirestoreDataSource
import com.dv.apna.feature.home.data.model.BannerDto
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class HomeDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : FirestoreDataSource(firestore) {

    suspend fun getBanners(villageId: String): List<BannerDto> {
        return fetchBlockDocuments(locationId = villageId, subCollection = "banners")
            .mapNotNull { doc ->
                doc.toObject(BannerDto::class.java)?.copy(id = doc.id)
            }
            .filter { !it.imageUrl.isNullOrBlank() || it.title != null }
            .distinctBy { it.id ?: (it.imageUrl ?: "") }
            .sortedBy { it.order ?: 99 }
    }
}
