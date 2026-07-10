package com.dv.apna.feature.construction.data.model

data class ConstructionDto(
    val id: String? = null,
    val shopName: Any? = null,
    val address: Any? = null,
    val contact: String? = null,
    val image: String? = null,
    val categoryId: String? = null,
    val villageId: String? = null,
    val products: List<ProductDto>? = null
)

data class ProductDto(
    val name: Any? = null,
    val price: String? = null,
    val unit: Any? = null
)
