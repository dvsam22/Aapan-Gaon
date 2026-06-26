package com.dv.apna.feature.home.data.repository

import com.dv.apna.core.common.Resource
import com.dv.apna.feature.home.data.datasource.HomeDataSource
import com.dv.apna.feature.home.domain.model.HomeModel
import com.dv.apna.feature.home.domain.repository.HomeRepository
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
