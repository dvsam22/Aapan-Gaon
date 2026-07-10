package com.dv.apna.feature.news.domain.repository

import com.dv.apna.core.common.Resource
import com.dv.apna.feature.news.domain.model.NewsModel
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    fun getNews(villageId: String): Flow<Resource<List<NewsModel>>>
}
