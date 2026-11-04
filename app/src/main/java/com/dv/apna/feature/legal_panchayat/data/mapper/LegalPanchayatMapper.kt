package com.dv.apna.feature.legal_panchayat.data.mapper

import com.dv.apna.core.utils.toLocalizedSafeString
import com.dv.apna.feature.legal_panchayat.data.model.LegalPanchayatDto
import com.dv.apna.feature.legal_panchayat.domain.model.LegalPanchayatDetails

fun LegalPanchayatDto.toDomain(languageCode: String): LegalPanchayatDetails {
    return LegalPanchayatDetails(
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
