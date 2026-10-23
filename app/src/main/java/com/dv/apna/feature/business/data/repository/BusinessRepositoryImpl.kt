package com.dv.apna.feature.business.data.repository

import com.dv.apna.core.common.Resource
import com.dv.apna.feature.business.data.datasource.BusinessDataSource
import com.dv.apna.feature.business.data.model.BusinessRegistrationDto
import com.dv.apna.feature.business.domain.model.BusinessRegistrationModel
import com.dv.apna.feature.business.domain.repository.BusinessRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class BusinessRepositoryImpl @Inject constructor(
    private val dataSource: BusinessDataSource
) : BusinessRepository {

    override fun submitBusiness(model: BusinessRegistrationModel): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val dto = BusinessRegistrationDto(
                contactName = model.contactName,
                businessName = model.businessName,
                category = model.category,
                phoneNumber = model.phoneNumber,
                address = model.address,
                description = model.description,
                villageId = model.villageId,
                villageName = model.villageName,
                status = "pending"
            )
            dataSource.submitRegistration(dto)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to submit details. Please try again."))
        }
    }
}
