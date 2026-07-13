package com.dv.apna.feature.family.domain.usecase

import com.dv.apna.feature.family.domain.repository.FamilyFunctionRepository
import javax.inject.Inject

class GetFamilyFunctionsByCategoryUseCase @Inject constructor(
    private val repository: FamilyFunctionRepository
) {
    operator fun invoke(villageId: String, categoryId: String) = repository.getFamilyFunctions(villageId, categoryId)
}
