package com.dv.apna.feature.news.domain.usecase

import com.dv.apna.feature.news.domain.repository.NewsRepository
import javax.inject.Inject

class GetNewsDataUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    operator fun invoke() = repository.getData()
}