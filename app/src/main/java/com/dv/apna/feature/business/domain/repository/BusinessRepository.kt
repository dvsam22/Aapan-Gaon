package com.dv.apna.feature.business.domain.repository

import com.dv.apna.core.common.Resource
import com.dv.apna.feature.business.domain.model.BusinessRegistrationModel
import kotlinx.coroutines.flow.Flow

interface BusinessRepository {
    fun submitBusiness(model: BusinessRegistrationModel): Flow<Resource<Unit>>
}
