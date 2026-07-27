package com.dv.apna.feature.construction.domain.model

data class ConstructionItemModel(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val phone: String = "",
    val image: String = "",
    val products: List<ConstructionProductModel> = emptyList()
)

data class ConstructionProductModel(
    val name: String = "",
    val price: String = "",
    val unit: String = ""
)
