package com.dv.apna.feature.construction.data.mapper

import com.dv.apna.feature.construction.data.model.ConstructionDto
import com.dv.apna.feature.construction.data.model.ProductDto
import com.dv.apna.feature.construction.domain.model.*

fun ConstructionDto.toBricksSupplier(): BricksSupplierModel {
    return BricksSupplierModel(
        id = id ?: "",
        name = shopName ?: "",
        address = address ?: "",
        phone = contact ?: "",
        brickTypes = products?.map { it.toBrickTypePrice() } ?: emptyList()
    )
}

fun ProductDto.toBrickTypePrice() = BrickTypePrice(
    name = name ?: "",
    price = price ?: "",
    unit = unit ?: ""
)

fun ConstructionDto.toMaterialShop(): MaterialShopModel {
    return MaterialShopModel(
        id = id ?: "",
        name = shopName ?: "",
        address = address ?: "",
        phone = contact ?: "",
        materials = products?.map { it.toMaterialTypePrice() } ?: emptyList()
    )
}

fun ProductDto.toMaterialTypePrice() = MaterialTypePrice(
    name = name ?: "",
    price = price ?: "",
    unit = unit ?: ""
)

fun ConstructionDto.toHardwareShop(): HardwareShopModel {
    return HardwareShopModel(
        id = id ?: "",
        name = shopName ?: "",
        address = address ?: "",
        phone = contact ?: "",
        items = products?.map { it.toHardwareItemPrice() } ?: emptyList()
    )
}

fun ProductDto.toHardwareItemPrice() = HardwareItemPrice(
    name = name ?: "",
    price = price ?: "",
    unit = unit ?: ""
)
