package com.example.aapangav.feature.construction.domain.usecase

import com.example.aapangav.feature.construction.domain.repository.ConstructionRepository
import javax.inject.Inject

class GetConstructionDataUseCase @Inject constructor(
    private val repository: ConstructionRepository
) {
    operator fun invoke() = repository.getData()
}