package com.example.aapangav.feature.home.domain.usecase

import com.example.aapangav.feature.home.domain.repository.HomeRepository
import javax.inject.Inject

class GetHomeDataUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    operator fun invoke() = repository.getHomeData()
}
