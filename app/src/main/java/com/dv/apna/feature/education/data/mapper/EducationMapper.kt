package com.dv.apna.feature.education.data.mapper

import com.dv.apna.core.utils.toLocalizedSafeString
import com.dv.apna.feature.education.data.model.EducationDto
import com.dv.apna.feature.education.domain.model.EducationDetails

fun EducationDto.toDomain(languageCode: String): EducationDetails {
    return EducationDetails(
        id = id ?: "",
        name = name.toLocalizedSafeString(languageCode),
        categoryId = categoryId ?: "",
        contact = contact ?: "",
        details = details.toLocalizedSafeString(languageCode),
        location = address.toLocalizedSafeString(languageCode),
        extraInfo = extraInfo.toLocalizedSafeString(languageCode),
        image = image ?: "",
        villageId = villageId ?: ""
    )
}
