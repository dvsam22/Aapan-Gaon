package com.dv.apna.feature.mandi.data.repository

import com.dv.apna.core.common.Resource
import com.dv.apna.feature.mandi.domain.model.MandiModel
import com.dv.apna.feature.mandi.domain.repository.MandiRepository
import com.dv.apna.feature.mandi.data.datasource.MandiDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MandiRepositoryImpl @Inject constructor(
    private val dataSource: MandiDataSource
) : MandiRepository {
    override fun getData(): Flow<Resource<List<MandiModel>>> = flow {
        emit(Resource.Loading())
    }
}