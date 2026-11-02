package com.dv.apna.feature.education.domain.usecase

import com.dv.apna.core.common.Resource
import com.dv.apna.feature.education.domain.model.EducationItemModel
import com.dv.apna.feature.education.domain.repository.EducationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetEducationItemsUseCase @Inject constructor(
    private val repository: EducationRepository
) {
    operator fun invoke(villageId: String, categoryId: String): Flow<Resource<List<EducationItemModel>>> {
        return repository.getEducationItems(villageId, categoryId)
    }
}
