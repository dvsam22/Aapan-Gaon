package com.dv.apna.feature.business.domain.usecase

import com.dv.apna.core.common.Resource
import com.dv.apna.feature.business.domain.model.BusinessRegistrationModel
import com.dv.apna.feature.business.domain.repository.BusinessRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SubmitBusinessUseCase @Inject constructor(
    private val repository: BusinessRepository
) {
    operator fun invoke(model: BusinessRegistrationModel): Flow<Resource<Unit>> {
        return repository.submitBusiness(model)
    }
}
