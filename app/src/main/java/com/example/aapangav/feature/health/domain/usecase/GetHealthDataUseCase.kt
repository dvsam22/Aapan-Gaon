package com.example.aapangav.feature.health.domain.usecase

import com.example.aapangav.feature.health.domain.repository.HealthRepository
import javax.inject.Inject

class GetHealthDataUseCase @Inject constructor(
    private val repository: HealthRepository
) {
    operator fun invoke() = repository.getData()
}