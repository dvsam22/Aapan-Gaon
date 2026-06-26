package com.example.aapangav.feature.home.data.repository

import com.example.aapangav.core.common.Resource
import com.example.aapangav.feature.home.data.datasource.HomeDataSource
import com.example.aapangav.feature.home.domain.model.HomeModel
import com.example.aapangav.feature.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val dataSource: HomeDataSource
) : HomeRepository {
    override fun getHomeData(): Flow<Resource<List<HomeModel>>> = flow {
        emit(Resource.Loading())
        // Implementation logic calling dataSource
    }
}
