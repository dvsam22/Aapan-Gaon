package com.dv.apna.feature.transport.data.mapper

import com.dv.apna.feature.transport.data.model.TransportDto
import com.dv.apna.feature.transport.domain.model.TransportDetails

fun TransportDto.toDomain(): TransportDetails {
    return TransportDetails(
        id = id,
        name = name,
        location = location,
        vehicleType = vehicleType,
        contact = contact,
        image = image
    )
}
