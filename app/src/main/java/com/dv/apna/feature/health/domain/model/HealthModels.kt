package com.dv.apna.feature.health.domain.model

data class DoctorModel(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val specialization: String = "",
    val availability: String = "",
    val phone: String = ""
)

data class HospitalModel(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val type: String = "",
    val facilities: String = "",
    val openStatus: String = "",
    val phone: String = ""
)

data class PharmacyModel(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val services: String = "",
    val openStatus: String = "",
    val phone: String = ""
)
