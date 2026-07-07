package com.dv.apna.feature.mandi.data.mapper

import com.dv.apna.feature.mandi.data.model.MandiDto
import com.dv.apna.feature.mandi.domain.model.CropPriceModel
import com.dv.apna.feature.mandi.domain.model.LocalBuyerModel
import com.dv.apna.feature.mandi.domain.model.MarketPriceModel

fun MandiDto.toCropPriceModel(): CropPriceModel {
    return CropPriceModel(
        id = id,
        name = cropName,
        unit = unit,
        price = price,
        trend = trend,
        date = date
    )
}

fun MandiDto.toMarketPriceModel(): MarketPriceModel {
    return MarketPriceModel(
        id = id,
        name = cropName,
        unit = unit,
        price = price,
        trend = trend,
        date = date
    )
}

fun MandiDto.toLocalBuyerModel(): LocalBuyerModel {
    return LocalBuyerModel(
        id = id,
        name = buyerName,
        address = address,
        contact = contact,
        category = cropName
    )
}
