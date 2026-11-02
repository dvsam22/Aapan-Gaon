package com.dv.apna.feature.dignitaries.domain.usecase

import com.dv.apna.core.common.Resource
import com.dv.apna.feature.dignitaries.domain.model.DignitaryItemModel
import com.dv.apna.feature.dignitaries.domain.repository.DignitariesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDignitariesUseCase @Inject constructor(
    private val repository: DignitariesRepository
) {
    operator fun invoke(villageId: String, categoryId: String): Flow<Resource<List<DignitaryItemModel>>> {
        return repository.getDignitaries(villageId, categoryId)
    }
}
