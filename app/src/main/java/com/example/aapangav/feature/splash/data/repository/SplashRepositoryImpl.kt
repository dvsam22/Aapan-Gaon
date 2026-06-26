package com.example.aapangav.feature.splash.data.repository

import com.example.aapangav.core.common.Resource
import com.example.aapangav.feature.splash.domain.model.SplashModel
import com.example.aapangav.feature.splash.domain.repository.SplashRepository
import com.example.aapangav.feature.splash.data.datasource.SplashDataSource
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