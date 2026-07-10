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
        return getCollection("villages")
            .document(villageId)
            .collection("news")
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .await()
            .toObjects(NewsDto::class.java)
    }
}
