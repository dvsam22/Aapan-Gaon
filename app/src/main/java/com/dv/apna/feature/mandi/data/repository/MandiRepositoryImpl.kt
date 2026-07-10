package com.dv.apna.feature.mandi.data.repository

import com.dv.apna.core.common.Resource
import com.dv.apna.core.datastore.PreferenceManager
import com.dv.apna.feature.mandi.data.datasource.MandiDataSource
import com.dv.apna.feature.mandi.data.mapper.toCropPriceModel
import com.dv.apna.feature.mandi.data.mapper.toLocalBuyerModel
import com.dv.apna.feature.mandi.data.mapper.toMarketPriceModel
import com.dv.apna.feature.mandi.domain.model.CropPriceModel
import com.dv.apna.feature.mandi.domain.model.LocalBuyerModel
import com.dv.apna.feature.mandi.domain.model.MarketPriceModel
import com.dv.apna.feature.mandi.domain.repository.MandiRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MandiRepositoryImpl @Inject constructor(
    private val dataSource: MandiDataSource,
    private val preferenceManager: PreferenceManager
) : MandiRepository {

    override fun getCropPrices(villageId: String): Flow<Resource<List<CropPriceModel>>> = flow {
        emit(Resource.Loading())
        try {
            val languageCode = preferenceManager.languageCode.firstOrNull() ?: "en"
            val data = dataSource.getMandiData(villageId, "prices")
                .map { it.toCropPriceModel(languageCode) }
            emit(Resource.Success(data))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error"))
        }
    }

    override fun getMarketPrices(villageId: String): Flow<Resource<List<MarketPriceModel>>> = flow {
        emit(Resource.Loading())
        try {
            val languageCode = preferenceManager.languageCode.firstOrNull() ?: "en"
            val data = dataSource.getMandiData(villageId, "market")
                .map { it.toMarketPriceModel(languageCode) }
            emit(Resource.Success(data))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error"))
        }
    }

    override fun getLocalBuyers(villageId: String): Flow<Resource<List<LocalBuyerModel>>> = flow {
        emit(Resource.Loading())
        try {
            val languageCode = preferenceManager.languageCode.firstOrNull() ?: "en"
            val data = dataSource.getMandiData(villageId, "buyers")
                .map { it.toLocalBuyerModel(languageCode) }
            emit(Resource.Success(data))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error"))
        }
    }
}
