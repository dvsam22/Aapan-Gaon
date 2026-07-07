package com.dv.apna.feature.labour.domain.usecase

import com.dv.apna.feature.labour.domain.repository.LabourRepository
import javax.inject.Inject

class GetLaboursByCategoryUseCase @Inject constructor(
    private val repository: LabourRepository
) {
    operator fun invoke(villageId: String, categoryId: String) = repository.getLabours(villageId, categoryId)
}
