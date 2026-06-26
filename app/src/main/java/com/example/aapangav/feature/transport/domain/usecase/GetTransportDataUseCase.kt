package com.example.aapangav.feature.transport.domain.usecase

import com.example.aapangav.feature.transport.domain.repository.TransportRepository
import javax.inject.Inject

class GetTransportDataUseCase @Inject constructor(
    private val repository: TransportRepository
) {
    operator fun invoke() = repository.getData()
}