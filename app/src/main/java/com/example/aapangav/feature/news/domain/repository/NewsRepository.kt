package com.example.aapangav.feature.news.domain.repository

import com.example.aapangav.core.common.Resource
import kotlinx.coroutines.flow.Flow
import com.example.aapangav.feature.news.domain.model.NewsModel

interface NewsRepository {
    fun getData(): Flow<Resource<List<NewsModel>>>
}