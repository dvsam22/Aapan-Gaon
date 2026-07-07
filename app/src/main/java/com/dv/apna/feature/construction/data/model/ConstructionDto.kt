package com.dv.apna.feature.construction.data.model

data class ConstructionDto(
    val id: String? = null,
    val shopName: String? = null,
    val address: String? = null,
    val contact: String? = null,
    val image: String? = null,
    val categoryId: String? = null,
    val villageId: String? = null,
    val products: List<ProductDto>? = null
)

data class ProductDto(
    val name: String? = null,
    val price: String? = null,
    val unit: String? = null
)
