package com.dv.apna.feature.news.domain.usecase

import com.dv.apna.core.common.Resource
import com.dv.apna.feature.news.domain.model.NewsModel
import com.dv.apna.feature.news.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNewsDataUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    operator fun invoke(villageId: String): Flow<Resource<List<NewsModel>>> {
        return repository.getNews(villageId)
    }
}
