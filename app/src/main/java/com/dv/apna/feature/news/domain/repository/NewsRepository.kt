package com.dv.apna.feature.news.domain.repository

import com.dv.apna.core.common.Resource
import kotlinx.coroutines.flow.Flow
import com.dv.apna.feature.news.domain.model.NewsModel

interface NewsRepository {
    fun getData(): Flow<Resource<List<NewsModel>>>
}