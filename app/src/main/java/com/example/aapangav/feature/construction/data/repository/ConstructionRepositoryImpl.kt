package com.example.aapangav.feature.construction.data.repository

import com.example.aapangav.core.common.Resource
import com.example.aapangav.feature.construction.domain.model.ConstructionModel
import com.example.aapangav.feature.construction.domain.repository.ConstructionRepository
import com.example.aapangav.feature.construction.data.datasource.ConstructionDataSource
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