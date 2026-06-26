package com.dv.apna.feature.news.data.repository

import com.dv.apna.core.common.Resource
import com.dv.apna.feature.news.domain.model.NewsModel
import com.dv.apna.feature.news.domain.repository.NewsRepository
import com.dv.apna.feature.news.data.datasource.NewsDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val dataSource: NewsDataSource
) : NewsRepository {
    override fun getData(): Flow<Resource<List<NewsModel>>> = flow {
        emit(Resource.Loading())
    }
}