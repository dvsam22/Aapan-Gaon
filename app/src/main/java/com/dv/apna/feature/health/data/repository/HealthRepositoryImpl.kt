package com.dv.apna.feature.health.data.repository

import com.dv.apna.core.common.Resource
import com.dv.apna.feature.health.domain.model.HealthModel
import com.dv.apna.feature.health.domain.repository.HealthRepository
import com.dv.apna.feature.health.data.datasource.HealthDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class HealthRepositoryImpl @Inject constructor(
    private val dataSource: HealthDataSource
) : HealthRepository {
    override fun getData(): Flow<Resource<List<HealthModel>>> = flow {
        emit(Resource.Loading())
    }
}