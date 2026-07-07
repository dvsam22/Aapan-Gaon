package com.dv.apna.feature.mandi.domain.model

data class MarketPriceModel(
    val id: String = "",
    val name: String = "",
    val unit: String = "1 Kg",
    val price: Double = 0.0,
    val trend: String = "stable",
    val date: Long = 0
)
