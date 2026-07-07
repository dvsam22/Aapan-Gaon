package com.dv.apna.feature.transport.domain.usecase

import com.dv.apna.feature.transport.domain.repository.TransportRepository
import javax.inject.Inject

class GetTransportByCategoryUseCase @Inject constructor(
    private val repository: TransportRepository
) {
    operator fun invoke(villageId: String, categoryId: String) = 
        repository.getTransportByCategory(villageId, categoryId)
}
