package com.dv.apna.feature.mandi.domain.model

data class CropPriceModel(
    val id: String = "",
    val name: String = "",
    val unit: String = "1 Quintal",
    val price: Double = 0.0,
    val trend: String = "stable",
    val date: Long = 0
)
