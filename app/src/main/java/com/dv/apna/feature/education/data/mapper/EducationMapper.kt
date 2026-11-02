package com.dv.apna.feature.education.data.mapper

import com.dv.apna.core.utils.toLocalizedSafeString
import com.dv.apna.feature.education.data.model.EducationDto
import com.dv.apna.feature.education.domain.model.EducationItemModel

fun EducationDto.toDomain(languageCode: String): EducationItemModel {
    val loc = location?.toLocalizedSafeString(languageCode)?.ifBlank {
        address.toLocalizedSafeString(languageCode)
    } ?: address.toLocalizedSafeString(languageCode)

    val desc = description?.toLocalizedSafeString(languageCode)?.ifBlank {
        subjects.toLocalizedSafeString(languageCode)
    } ?: subjects.toLocalizedSafeString(languageCode)

    return EducationItemModel(
        id = id ?: "",
        name = name.toLocalizedSafeString(languageCode),
        categoryId = categoryId ?: "",
        roleOrType = roleOrType.toLocalizedSafeString(languageCode),
        contact = contact ?: "",
        whatsapp = whatsapp ?: contact ?: "",
        location = loc,
        description = desc,
        extraInfo = extraInfo.toLocalizedSafeString(languageCode),
        image = image ?: "",
        fileUrl = fileUrl ?: "",
        villageId = villageId ?: ""
    )
}
