package com.dv.apna.feature.mandi.presentation.state

import com.dv.apna.feature.mandi.domain.model.CropPriceModel
import com.dv.apna.feature.mandi.domain.model.LocalBuyerModel
import com.dv.apna.feature.mandi.domain.model.MarketPriceModel

data class MandiState(
    val isLoading: Boolean = false,
    val cropPrices: List<CropPriceModel> = emptyList(),
    val marketPrices: List<MarketPriceModel> = emptyList(),
    val localBuyers: List<LocalBuyerModel> = emptyList(),
    val error: String? = null
)
