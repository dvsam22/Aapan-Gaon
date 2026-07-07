package com.dv.apna.feature.construction.domain.model

data class HardwareShopModel(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val phone: String = "",
    val items: List<HardwareItemPrice> = emptyList()
)

data class HardwareItemPrice(
    val name: String = "",
    val price: String = "",
    val unit: String = ""
)
