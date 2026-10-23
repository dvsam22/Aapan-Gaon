package com.dv.apna.feature.mandi.data.mapper

import com.dv.apna.core.utils.toLocalizedSafeString
import com.dv.apna.feature.mandi.data.model.MandiDto
import com.dv.apna.feature.mandi.domain.model.CropPriceModel
import com.dv.apna.feature.mandi.domain.model.LocalBuyerModel
import com.dv.apna.feature.mandi.domain.model.MarketPriceModel

fun MandiDto.toCropPriceModel(languageCode: String): CropPriceModel {
    val baseName = cropName.toLocalizedSafeString(languageCode)
    val marketStr = market.toLocalizedSafeString(languageCode)
    val displayName = if (marketStr.isNotBlank()) "$baseName • $marketStr" else baseName
    return CropPriceModel(
        id = id,
        name = displayName,
        unit = unit.toLocalizedSafeString(languageCode).ifBlank { "1 Quintal" },
        price = price,
        trend = trend.toLocalizedSafeString(languageCode).ifBlank { "stable" },
        date = date
    )
}

fun MandiDto.toMarketPriceModel(languageCode: String): MarketPriceModel {
    val baseName = cropName.toLocalizedSafeString(languageCode)
    val marketStr = market.toLocalizedSafeString(languageCode)
    val displayName = if (marketStr.isNotBlank()) "$baseName • $marketStr" else baseName
    return MarketPriceModel(
        id = id,
        name = displayName,
        unit = unit.toLocalizedSafeString(languageCode).ifBlank { "1 Quintal" },
        price = price,
        trend = trend.toLocalizedSafeString(languageCode).ifBlank { "stable" },
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
