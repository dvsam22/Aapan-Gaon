package com.dv.apna.feature.construction.domain.usecase

import com.dv.apna.core.common.Resource
import com.dv.apna.feature.construction.domain.model.ConstructionItemModel
import com.dv.apna.feature.construction.domain.repository.ConstructionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetConstructionItemsUseCase @Inject constructor(
    private val repository: ConstructionRepository
) {
    operator fun invoke(villageId: String, categoryId: String): Flow<Resource<List<ConstructionItemModel>>> {
        return repository.getConstructionItems(villageId, categoryId)
    }
}
