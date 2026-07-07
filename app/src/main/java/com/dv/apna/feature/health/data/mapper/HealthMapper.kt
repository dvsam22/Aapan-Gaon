package com.dv.apna.feature.health.data.mapper

import com.dv.apna.feature.health.data.model.HealthDto
import com.dv.apna.feature.health.domain.model.DoctorModel
import com.dv.apna.feature.health.domain.model.HospitalModel
import com.dv.apna.feature.health.domain.model.PharmacyModel

fun HealthDto.toDoctorModel(): DoctorModel {
    return DoctorModel(
        id = id,
        name = name,
        address = address,
        specialization = specialisation,
        availability = availability,
        phone = contact
    )
}

fun HealthDto.toHospitalModel(): HospitalModel {
    return HospitalModel(
        id = id,
        name = name,
        address = address,
        type = type,
        facilities = facilities,
        openStatus = availability,
        phone = contact
    )
}

fun HealthDto.toPharmacyModel(): PharmacyModel {
    return PharmacyModel(
        id = id,
        name = name,
        address = address,
        services = services,
        openStatus = availability,
        phone = contact
    )
}

fun HealthDto.toGenericModel(): DoctorModel { // For ambulance/police if using same model
    return DoctorModel(
        id = id,
        name = name,
        address = address,
        specialization = type,
        availability = availability,
        phone = contact
    )
}
