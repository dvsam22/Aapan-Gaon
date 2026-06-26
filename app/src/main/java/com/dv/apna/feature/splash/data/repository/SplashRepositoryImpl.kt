package com.dv.apna.feature.splash.data.repository

import com.dv.apna.core.common.Resource
import com.dv.apna.feature.splash.domain.model.SplashModel
import com.dv.apna.feature.splash.domain.repository.SplashRepository
import com.dv.apna.feature.splash.data.datasource.SplashDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SplashRepositoryImpl @Inject constructor(
    private val dataSource: SplashDataSource
) : SplashRepository {
    override fun getData(): Flow<Resource<List<SplashModel>>> = flow {
        emit(Resource.Loading())
    }
}