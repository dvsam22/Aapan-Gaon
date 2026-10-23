package com.dv.apna.feature.news.data.datasource

import com.dv.apna.core.firestore.FirestoreDataSource
import com.dv.apna.feature.news.data.model.NewsDto
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NewsDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : FirestoreDataSource(firestore) {

    suspend fun getNews(villageId: String): List<NewsDto> {
        val villageNews = try {
            getCollection("villages")
                .document(villageId)
                .collection("news")
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(30)
                .get()
                .await()
                .documents.mapNotNull { doc ->
                    doc.toObject(NewsDto::class.java)?.copy(id = doc.id)
                }
        } catch (e: Exception) {
            emptyList()
        }

        if (villageNews.isNotEmpty()) {
            return villageNews
        }

        // Fallback to central news collection if village-specific collection is empty
        return try {
            getCollection("news")
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(30)
                .get()
                .await()
                .documents.mapNotNull { doc ->
                    doc.toObject(NewsDto::class.java)?.copy(id = doc.id)
                }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
