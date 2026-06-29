package com.dv.apna.feature.construction.domain.model

data class BricksSupplierModel(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val brickTypes: List<BrickTypePrice> = emptyList()
)

data class BrickTypePrice(
    val name: String = "",
    val price: String = ""
)
