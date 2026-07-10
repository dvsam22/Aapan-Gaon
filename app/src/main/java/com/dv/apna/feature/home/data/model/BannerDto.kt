package com.dv.apna.feature.home.data.model

import androidx.annotation.Keep

@Keep
data class BannerDto(
    val discountText: String? = null,
    val id: String? = null,
    val imageUrl: String? = null,
    val link: String? = null,
    val title: Any? = null,
    val villageId: String? = null
)
