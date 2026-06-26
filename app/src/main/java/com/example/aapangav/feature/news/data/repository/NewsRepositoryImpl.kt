package com.example.aapangav.feature.news.data.repository

import com.example.aapangav.core.common.Resource
import com.example.aapangav.feature.news.domain.model.NewsModel
import com.example.aapangav.feature.news.domain.repository.NewsRepository
import com.example.aapangav.feature.news.data.datasource.NewsDataSource
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