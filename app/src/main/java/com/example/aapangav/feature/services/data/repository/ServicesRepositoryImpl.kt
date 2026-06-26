package com.example.aapangav.feature.services.data.repository

import com.example.aapangav.core.common.Resource
import com.example.aapangav.feature.services.domain.model.ServicesModel
import com.example.aapangav.feature.services.domain.repository.ServicesRepository
import com.example.aapangav.feature.services.data.datasource.ServicesDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ServicesRepositoryImpl @Inject constructor(
    private val dataSource: ServicesDataSource
) : ServicesRepository {
    override fun getData(): Flow<Resource<List<ServicesModel>>> = flow {
        emit(Resource.Loading())
    }
}