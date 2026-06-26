package com.dv.apna.feature.transport.data.repository

import com.dv.apna.core.common.Resource
import com.dv.apna.feature.transport.domain.model.TransportModel
import com.dv.apna.feature.transport.domain.repository.TransportRepository
import com.dv.apna.feature.transport.data.datasource.TransportDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TransportRepositoryImpl @Inject constructor(
    private val dataSource: TransportDataSource
) : TransportRepository {
    override fun getData(): Flow<Resource<List<TransportModel>>> = flow {
        emit(Resource.Loading())
    }
}