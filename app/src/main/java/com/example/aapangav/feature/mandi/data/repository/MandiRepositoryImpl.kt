package com.example.aapangav.feature.mandi.data.repository

import com.example.aapangav.core.common.Resource
import com.example.aapangav.feature.mandi.domain.model.MandiModel
import com.example.aapangav.feature.mandi.domain.repository.MandiRepository
import com.example.aapangav.feature.mandi.data.datasource.MandiDataSource
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