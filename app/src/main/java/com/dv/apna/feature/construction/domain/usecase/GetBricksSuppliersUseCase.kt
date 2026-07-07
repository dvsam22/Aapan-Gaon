package com.dv.apna.feature.construction.domain.usecase

import com.dv.apna.feature.construction.domain.repository.ConstructionRepository
import javax.inject.Inject

class GetBricksSuppliersUseCase @Inject constructor(
    private val repository: ConstructionRepository
) {
    operator fun invoke(villageId: String) = repository.getBricksSuppliers(villageId)
}
