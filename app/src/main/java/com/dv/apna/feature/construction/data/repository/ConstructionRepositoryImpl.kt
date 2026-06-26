package com.dv.apna.feature.construction.data.repository

import com.dv.apna.core.common.Resource
import com.dv.apna.feature.construction.domain.model.ConstructionModel
import com.dv.apna.feature.construction.domain.repository.ConstructionRepository
import com.dv.apna.feature.construction.data.datasource.ConstructionDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ConstructionRepositoryImpl @Inject constructor(
    private val dataSource: ConstructionDataSource
) : ConstructionRepository {
    override fun getData(): Flow<Resource<List<ConstructionModel>>> = flow {
        emit(Resource.Loading())
    }
}