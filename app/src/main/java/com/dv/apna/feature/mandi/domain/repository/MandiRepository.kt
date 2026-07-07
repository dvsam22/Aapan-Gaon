package com.dv.apna.feature.mandi.domain.repository

import com.dv.apna.core.common.Resource
import com.dv.apna.feature.mandi.domain.model.CropPriceModel
import com.dv.apna.feature.mandi.domain.model.LocalBuyerModel
import com.dv.apna.feature.mandi.domain.model.MarketPriceModel
import kotlinx.coroutines.flow.Flow

interface MandiRepository {
    fun getCropPrices(villageId: String): Flow<Resource<List<CropPriceModel>>>
    fun getMarketPrices(villageId: String): Flow<Resource<List<MarketPriceModel>>>
    fun getLocalBuyers(villageId: String): Flow<Resource<List<LocalBuyerModel>>>
}
