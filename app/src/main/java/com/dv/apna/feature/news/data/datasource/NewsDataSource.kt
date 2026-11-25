package com.dv.apna.feature.news.data.datasource

import com.dv.apna.core.firestore.FirestoreDataSource
import com.dv.apna.feature.news.data.model.NewsDto
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class NewsDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : FirestoreDataSource(firestore) {

    suspend fun getNews(villageId: String): List<NewsDto> {
        return fetchBlockDocuments(locationId = villageId, subCollection = "news")
            .mapNotNull { doc ->
                doc.toObject(NewsDto::class.java)?.copy(id = doc.id)
            }
            .sortedByDescending { it.date ?: 0L }
            .take(30)
    }
}
