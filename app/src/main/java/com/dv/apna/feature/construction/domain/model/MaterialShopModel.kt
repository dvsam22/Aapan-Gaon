package com.dv.apna.feature.construction.domain.model

data class MaterialShopModel(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val materials: List<MaterialTypePrice> = emptyList()
)

data class MaterialTypePrice(
    val name: String = "",
    val price: String = ""
)
