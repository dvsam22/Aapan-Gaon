package com.dv.apna.feature.labour.domain.model

data class LabourDetails(
    val id: String = "",
    val name: String,
    val address: String,
    val skills: String,
    val charges: String,
    val phoneNumber: String
)
