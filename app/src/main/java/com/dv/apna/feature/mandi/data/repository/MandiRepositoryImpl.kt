package com.dv.apna.feature.mandi.data.repository

import com.dv.apna.core.common.Resource
import com.dv.apna.core.datastore.PreferenceManager
import com.dv.apna.feature.mandi.data.datasource.MandiDataSource
import com.dv.apna.feature.mandi.data.mapper.toLocalBuyerModel
import com.dv.apna.feature.mandi.data.mapper.toMarketPriceModel
import com.dv.apna.feature.mandi.domain.model.LocalBuyerModel
import com.dv.apna.feature.mandi.domain.model.MarketPriceModel
import com.dv.apna.feature.mandi.domain.repository.MandiRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MandiRepositoryImpl @Inject constructor(
    private val dataSource: MandiDataSource,
    private val preferenceManager: PreferenceManager
) : MandiRepository {

    private var cachedMarketPrices: Pair<String, List<MarketPriceModel>>? = null
    private var cachedLocalBuyers: Pair<String, List<LocalBuyerModel>>? = null

    override fun getMarketPrices(villageId: String): Flow<Resource<List<MarketPriceModel>>> = flow {
        val languageCode = preferenceManager.languageCode.firstOrNull() ?: "en"
        val cacheKey = "$villageId-$languageCode"

        cachedMarketPrices?.let { (key, data) ->
            if (key == cacheKey && data.isNotEmpty()) {
                emit(Resource.Success(data))
            } else {
                emit(Resource.Loading())
            }
        } ?: emit(Resource.Loading())

        try {
            val data = dataSource.getAllMandiPrices(villageId)
                .map { it.toMarketPriceModel(languageCode) }
            cachedMarketPrices = cacheKey to data
            emit(Resource.Success(data))
        } catch (e: Exception) {
            if (cachedMarketPrices?.first != cacheKey) {
                emit(Resource.Error(e.message ?: "Unknown error"))
            }
        }
    }

    override fun getLocalBuyers(villageId: String): Flow<Resource<List<LocalBuyerModel>>> = flow {
        val languageCode = preferenceManager.languageCode.firstOrNull() ?: "en"
        val cacheKey = "$villageId-$languageCode"

        cachedLocalBuyers?.let { (key, data) ->
            if (key == cacheKey && data.isNotEmpty()) {
                emit(Resource.Success(data))
            } else {
                emit(Resource.Loading())
            }
        } ?: emit(Resource.Loading())

        try {
            val data = dataSource.getMandiData(villageId, "buyers")
                .map { it.toLocalBuyerModel(languageCode) }
            cachedLocalBuyers = cacheKey to data
            emit(Resource.Success(data))
        } catch (e: Exception) {
            if (cachedLocalBuyers?.first != cacheKey) {
                emit(Resource.Error(e.message ?: "Unknown error"))
            }
        }
    }
}
