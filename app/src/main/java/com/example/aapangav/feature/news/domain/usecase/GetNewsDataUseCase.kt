package com.example.aapangav.feature.news.domain.usecase

import com.example.aapangav.feature.news.domain.repository.NewsRepository
import javax.inject.Inject

class GetNewsDataUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    operator fun invoke() = repository.getData()
}