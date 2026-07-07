package com.dv.apna.feature.transport.data.repository

import com.dv.apna.core.common.Resource
import com.dv.apna.feature.transport.data.datasource.TransportDataSource
import com.dv.apna.feature.transport.data.mapper.toDomain
import com.dv.apna.feature.transport.domain.model.TransportDetails
import com.dv.apna.feature.transport.domain.repository.TransportRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TransportRepositoryImpl @Inject constructor(
    private val dataSource: TransportDataSource
) : TransportRepository {

    override fun getTransportByCategory(
        villageId: String,
        categoryId: String
    ): Flow<Resource<List<TransportDetails>>> = flow {
        emit(Resource.Loading())
        try {
            val data = dataSource.getTransportData(villageId, categoryId)
                .map { it.toDomain() }
            emit(Resource.Success(data))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error"))
        }
    }
}
