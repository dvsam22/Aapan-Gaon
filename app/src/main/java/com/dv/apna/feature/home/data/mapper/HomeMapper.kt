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
    val finalImageUrl = if (id == "banner_buyers" || link == "local_buyers" || link == "buyers" || (imageUrl?.contains("photo-1500937386664") == true)) {
        "https://images.unsplash.com/photo-1576045057995-568f588f82fb?w=800&auto=format&fit=crop"
    } else {
        imageUrl ?: ""
    }
    return BannerModel(
        id = id ?: "",
        imageUrl = finalImageUrl,
        title = title.toLocalizedSafeString(languageCode),
        discountText = discountText ?: "",
        link = link ?: "",
        villageId = villageId ?: ""
    )
}
