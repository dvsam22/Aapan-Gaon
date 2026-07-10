package com.dv.apna.feature.mandi.data.mapper

import com.dv.apna.core.utils.toLocalizedSafeString
import com.dv.apna.feature.mandi.data.model.MandiDto
import com.dv.apna.feature.mandi.domain.model.CropPriceModel
import com.dv.apna.feature.mandi.domain.model.LocalBuyerModel
import com.dv.apna.feature.mandi.domain.model.MarketPriceModel

fun MandiDto.toCropPriceModel(languageCode: String): CropPriceModel {
    return CropPriceModel(
        id = id,
        name = cropName.toLocalizedSafeString(languageCode),
        unit = unit.toLocalizedSafeString(languageCode),
        price = price,
        trend = trend.toLocalizedSafeString(languageCode),
        date = date
    )
}

fun MandiDto.toMarketPriceModel(languageCode: String): MarketPriceModel {
    return MarketPriceModel(
        id = id,
        name = cropName.toLocalizedSafeString(languageCode),
        unit = unit.toLocalizedSafeString(languageCode),
        price = price,
        trend = trend.toLocalizedSafeString(languageCode),
        date = date
    )
}

fun MandiDto.toLocalBuyerModel(languageCode: String): LocalBuyerModel {
    return LocalBuyerModel(
        id = id,
        name = buyerName.toLocalizedSafeString(languageCode),
        address = address.toLocalizedSafeString(languageCode),
        contact = contact,
        category = cropName.toLocalizedSafeString(languageCode)
    )
}
