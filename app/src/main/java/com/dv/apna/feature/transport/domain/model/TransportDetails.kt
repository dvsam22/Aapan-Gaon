package com.dv.apna.feature.transport.domain.model

data class TransportDetails(
    val id: String = "",
    val name: String,
    val address: String,
    val vehicleType: String,
    val charges: String,
    val phoneNumber: String
)
