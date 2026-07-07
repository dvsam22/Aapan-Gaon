package com.dv.apna.feature.construction.domain.usecase

import com.dv.apna.feature.construction.domain.repository.ConstructionRepository
import javax.inject.Inject

class GetHardwareShopsUseCase @Inject constructor(
    private val repository: ConstructionRepository
) {
    operator fun invoke(villageId: String) = repository.getHardwareShops(villageId)
}
