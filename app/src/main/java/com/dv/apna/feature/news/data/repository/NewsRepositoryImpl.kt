package com.dv.apna.feature.news.data.repository

import com.dv.apna.core.common.Resource
import com.dv.apna.core.datastore.PreferenceManager
import com.dv.apna.feature.news.data.datasource.NewsDataSource
import com.dv.apna.feature.news.data.mapper.toDomain
import com.dv.apna.feature.news.domain.model.NewsModel
import com.dv.apna.feature.news.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val dataSource: NewsDataSource,
    private val preferenceManager: PreferenceManager
) : NewsRepository {
    override fun getNews(villageId: String): Flow<Resource<List<NewsModel>>> = flow {
        emit(Resource.Loading())
        try {
            val languageCode = preferenceManager.languageCode.firstOrNull() ?: "en"
            val news = dataSource.getNews(villageId).map { it.toDomain(languageCode) }
            emit(Resource.Success(news))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An error occurred"))
        }
    }
}
