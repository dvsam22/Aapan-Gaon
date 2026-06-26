package com.example.aapangav.feature.services.domain.usecase

import com.example.aapangav.feature.services.domain.repository.ServicesRepository
import javax.inject.Inject

class GetServicesDataUseCase @Inject constructor(
    private val repository: ServicesRepository
) {
    operator fun invoke() = repository.getData()
}