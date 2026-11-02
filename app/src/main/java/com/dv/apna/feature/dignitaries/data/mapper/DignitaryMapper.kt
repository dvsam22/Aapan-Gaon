package com.dv.apna.feature.dignitaries.data.mapper

import com.dv.apna.core.utils.toLocalizedSafeString
import com.dv.apna.feature.dignitaries.data.model.DignitaryDto
import com.dv.apna.feature.dignitaries.domain.model.DignitaryItemModel

fun DignitaryDto.toDomain(languageCode: String): DignitaryItemModel {
    val desig = designation?.toLocalizedSafeString(languageCode)?.ifBlank {
        role.toLocalizedSafeString(languageCode)
    } ?: role.toLocalizedSafeString(languageCode)

    val phoneNum = contact?.ifBlank { phone } ?: phone ?: ""

    val loc = location?.toLocalizedSafeString(languageCode)?.ifBlank {
        address.toLocalizedSafeString(languageCode)
    } ?: address.toLocalizedSafeString(languageCode)

    return DignitaryItemModel(
        id = id ?: "",
        name = name.toLocalizedSafeString(languageCode),
        categoryId = categoryId ?: "",
        designation = desig,
        contact = phoneNum,
        whatsapp = whatsapp ?: phoneNum,
        location = loc,
        officeAddress = officeAddress.toLocalizedSafeString(languageCode),
        specialization = specialization.toLocalizedSafeString(languageCode),
        timings = timings.toLocalizedSafeString(languageCode),
        image = image ?: "",
        villageId = villageId ?: ""
    )
}
