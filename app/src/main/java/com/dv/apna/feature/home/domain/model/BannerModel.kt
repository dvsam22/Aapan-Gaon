package com.dv.apna.feature.home.domain.model

data class BannerModel(
    val id: String,
    val imageUrl: String,
    val title: String,
    val discountText: String,
    val link: String,
    val villageId: String
)
