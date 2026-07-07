package com.dv.apna.feature.mandi.domain.model

data class LocalBuyerModel(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val contact: String = "",
    val category: String = "" // This is cropName in the buyers category
)
