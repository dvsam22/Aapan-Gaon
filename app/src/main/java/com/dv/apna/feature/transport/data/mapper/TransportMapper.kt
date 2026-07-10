package com.dv.apna.feature.transport.data.mapper

import com.dv.apna.core.utils.toLocalizedSafeString
import com.dv.apna.feature.transport.data.model.TransportDto
import com.dv.apna.feature.transport.domain.model.TransportDetails

fun TransportDto.toDomain(languageCode: String): TransportDetails {
    return TransportDetails(
        id = id,
        name = name.toLocalizedSafeString(languageCode),
        location = location.toLocalizedSafeString(languageCode),
        vehicleType = vehicleType.toLocalizedSafeString(languageCode),
        contact = contact,
        image = image
    )
}
