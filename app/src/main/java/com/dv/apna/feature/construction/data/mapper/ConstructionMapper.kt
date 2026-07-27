package com.dv.apna.feature.construction.data.mapper

import com.dv.apna.core.utils.toLocalizedSafeString
import com.dv.apna.feature.construction.data.model.ConstructionDto
import com.dv.apna.feature.construction.data.model.ProductDto
import com.dv.apna.feature.construction.domain.model.*

fun ConstructionDto.toBricksSupplier(languageCode: String): BricksSupplierModel {
    return BricksSupplierModel(
        id = id ?: "",
        name = shopName.toLocalizedSafeString(languageCode),
        address = address.toLocalizedSafeString(languageCode),
        phone = contact ?: "",
        brickTypes = products?.map { it.toBrickTypePrice(languageCode) } ?: emptyList()
    )
}

fun ProductDto.toBrickTypePrice(languageCode: String) = BrickTypePrice(
    name = name.toLocalizedSafeString(languageCode),
    price = price ?: "",
    unit = unit.toLocalizedSafeString(languageCode)
)

fun ConstructionDto.toMaterialShop(languageCode: String): MaterialShopModel {
    return MaterialShopModel(
        id = id ?: "",
        name = shopName.toLocalizedSafeString(languageCode),
        address = address.toLocalizedSafeString(languageCode),
        phone = contact ?: "",
        materials = products?.map { it.toMaterialTypePrice(languageCode) } ?: emptyList()
    )
}

fun ProductDto.toMaterialTypePrice(languageCode: String) = MaterialTypePrice(
    name = name.toLocalizedSafeString(languageCode),
    price = price ?: "",
    unit = unit.toLocalizedSafeString(languageCode)
)

fun ConstructionDto.toHardwareShop(languageCode: String): HardwareShopModel {
    return HardwareShopModel(
        id = id ?: "",
        name = shopName.toLocalizedSafeString(languageCode),
        address = address.toLocalizedSafeString(languageCode),
        phone = contact ?: "",
        items = products?.map { it.toHardwareItemPrice(languageCode) } ?: emptyList()
    )
}

fun ProductDto.toHardwareItemPrice(languageCode: String) = HardwareItemPrice(
    name = name.toLocalizedSafeString(languageCode),
    price = price ?: "",
    unit = unit.toLocalizedSafeString(languageCode)
)

fun ConstructionDto.toConstructionItem(languageCode: String): ConstructionItemModel {
    return ConstructionItemModel(
        id = id ?: "",
        name = shopName.toLocalizedSafeString(languageCode),
        address = address.toLocalizedSafeString(languageCode),
        phone = contact ?: "",
        image = image ?: "",
        products = products?.map { it.toConstructionProduct(languageCode) } ?: emptyList()
    )
}

fun ProductDto.toConstructionProduct(languageCode: String) = ConstructionProductModel(
    name = name.toLocalizedSafeString(languageCode),
    price = price ?: "",
    unit = unit.toLocalizedSafeString(languageCode)
)
