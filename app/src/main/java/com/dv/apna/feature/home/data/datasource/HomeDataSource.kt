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
        val villageBanners = try {
            getCollection("villages")
                .document(villageId)
                .collection("banners")
                .get()
                .await()
                .toObjects(BannerDto::class.java)
        } catch (e: Exception) {
            emptyList()
        }

        val centralBanners = try {
            getCollection("banners")
                .get()
                .await()
                .toObjects(BannerDto::class.java)
                .sortedBy { it.order ?: 99 }
        } catch (e: Exception) {
            emptyList()
        }

        // Village banners come first, followed by central banners
        val combined = (villageBanners + centralBanners)
            .filter { !it.imageUrl.isNullOrBlank() || it.title != null }
            .distinctBy { it.id ?: (it.imageUrl ?: "") }

        return combined
    }
}
