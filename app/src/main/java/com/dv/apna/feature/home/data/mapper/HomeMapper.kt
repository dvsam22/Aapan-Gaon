package com.dv.apna.feature.home.data.mapper

import com.dv.apna.R
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
    val matchedDrawable = when (id) {
        "banner_mandi", "def_mandi" -> R.drawable.banner_mandi_rates
        "banner_news", "def_news" -> R.drawable.banner_local_news
        "banner_business", "def_business" -> R.drawable.banner_register_business
        "banner_buyers", "def_buyers" -> R.drawable.banner_fresh_produce
        "banner_emergency", "def_emergency" -> R.drawable.banner_emergency_health
        else -> when (link?.lowercase()?.trim()) {
            "today_market", "mandi" -> R.drawable.banner_mandi_rates
            "news" -> R.drawable.banner_local_news
            "add_business" -> R.drawable.banner_register_business
            "local_buyers" -> R.drawable.banner_fresh_produce
            "health", "emergency" -> R.drawable.banner_emergency_health
            else -> null
        }
    }

    return BannerModel(
        id = id ?: "",
        imageUrl = imageUrl ?: "",
        title = title.toLocalizedSafeString(languageCode),
        discountText = discountText.toLocalizedSafeString(languageCode),
        link = link ?: "",
        villageId = villageId ?: "",
        drawableRes = matchedDrawable
    )
}
