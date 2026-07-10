package com.dv.apna.feature.labour.data.mapper

import com.dv.apna.core.utils.toLocalizedSafeString
import com.dv.apna.feature.labour.data.model.LabourDto
import com.dv.apna.feature.labour.domain.model.LabourDetails

fun LabourDto.toDomain(languageCode: String): LabourDetails {
    return LabourDetails(
        id = id ?: "",
        name = name.toLocalizedSafeString(languageCode),
        categoryId = categoryId ?: "",
        charges = charges.toLocalizedSafeString(languageCode),
        contact = contact ?: "",
        experience = experience.toLocalizedSafeString(languageCode),
        image = image ?: "",
        location = location.toLocalizedSafeString(languageCode),
        skills = skills.toLocalizedSafeString(languageCode),
        villageId = villageId ?: ""
    )
}
