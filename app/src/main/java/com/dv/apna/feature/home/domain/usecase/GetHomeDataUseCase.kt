package com.dv.apna.feature.home.domain.usecase

import com.dv.apna.feature.home.domain.repository.HomeRepository
import javax.inject.Inject

class GetHomeDataUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    operator fun invoke() = repository.getHomeData()
}
