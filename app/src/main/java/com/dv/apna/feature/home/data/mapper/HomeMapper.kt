package com.dv.apna.feature.home.data.mapper

import com.dv.apna.core.utils.toLocalizedSafeString
import com.dv.apna.feature.home.data.model.BannerDto
import com.dv.apna.feature.home.data.model.HomeDto
import com.dv.apna.feature.home.domain.model.BannerModel
import com.dv.apna.feature.home.domain.model.HomeModel

fun HomeDto.toDomain(languageCode: String): HomeModel {
    return HomeModel(
        id = id ?: "",
        title = title ?: "",
        description = description ?: ""
    )
}

fun BannerDto.toDomain(languageCode: String): BannerModel {
    return BannerModel(
        id = id ?: "",
        imageUrl = imageUrl ?: "",
        title = title.toLocalizedSafeString(languageCode),
        discountText = discountText ?: "",
        link = link ?: "",
        villageId = villageId ?: ""
    )
}
