package com.dv.apna.feature.transport.domain.usecase

import com.dv.apna.feature.transport.domain.repository.TransportRepository
import javax.inject.Inject

class GetTransportDataUseCase @Inject constructor(
    private val repository: TransportRepository
) {
    operator fun invoke() = repository.getData()
}