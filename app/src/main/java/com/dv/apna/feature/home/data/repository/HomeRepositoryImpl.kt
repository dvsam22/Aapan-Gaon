package com.dv.apna.feature.home.data.repository

import com.dv.apna.core.common.Resource
import com.dv.apna.core.datastore.PreferenceManager
import com.dv.apna.feature.home.data.datasource.HomeDataSource
import com.dv.apna.feature.home.data.mapper.toDomain
import com.dv.apna.feature.home.domain.model.BannerModel
import com.dv.apna.feature.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val dataSource: HomeDataSource,
    private val preferenceManager: PreferenceManager
) : HomeRepository {
    override fun getBanners(villageId: String): Flow<Resource<List<BannerModel>>> = flow {
        emit(Resource.Loading())
        try {
            val languageCode = preferenceManager.languageCode.firstOrNull() ?: "en"
            val banners = dataSource.getBanners(villageId).map { it.toDomain(languageCode) }
            emit(Resource.Success(banners))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An error occurred"))
        }
    }
}
