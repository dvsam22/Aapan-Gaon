package com.dv.apna.feature.family.data.mapper

import com.dv.apna.core.utils.toLocalizedSafeString
import com.dv.apna.feature.family.data.model.FamilyFunctionDto
import com.dv.apna.feature.family.domain.model.FamilyFunctionDetails

fun FamilyFunctionDto.toDomain(languageCode: String): FamilyFunctionDetails {
    return FamilyFunctionDetails(
        id = id ?: "",
        name = name.toLocalizedSafeString(languageCode),
        categoryId = categoryId ?: "",
        startingPrice = startingPrice.toLocalizedSafeString(languageCode),
        contact = contact ?: "",
        image = image ?: "",
        location = address.toLocalizedSafeString(languageCode),
        services = services.toLocalizedSafeString(languageCode),
        villageId = villageId ?: ""
    )
}
