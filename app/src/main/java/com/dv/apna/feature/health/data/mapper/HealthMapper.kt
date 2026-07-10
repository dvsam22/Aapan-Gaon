package com.dv.apna.feature.health.data.mapper

import com.dv.apna.core.utils.toLocalizedSafeString
import com.dv.apna.feature.health.data.model.HealthDto
import com.dv.apna.feature.health.domain.model.DoctorModel
import com.dv.apna.feature.health.domain.model.HospitalModel
import com.dv.apna.feature.health.domain.model.PharmacyModel

fun HealthDto.toDoctorModel(languageCode: String): DoctorModel {
    return DoctorModel(
        id = id,
        name = name.toLocalizedSafeString(languageCode),
        address = address.toLocalizedSafeString(languageCode),
        specialization = specialisation.toLocalizedSafeString(languageCode),
        availability = availability.toLocalizedSafeString(languageCode),
        phone = contact
    )
}

fun HealthDto.toHospitalModel(languageCode: String): HospitalModel {
    return HospitalModel(
        id = id,
        name = name.toLocalizedSafeString(languageCode),
        address = address.toLocalizedSafeString(languageCode),
        type = type, // Usually internal ID
        facilities = facilities.toLocalizedSafeString(languageCode),
        openStatus = availability.toLocalizedSafeString(languageCode),
        phone = contact
    )
}

fun HealthDto.toPharmacyModel(languageCode: String): PharmacyModel {
    return PharmacyModel(
        id = id,
        name = name.toLocalizedSafeString(languageCode),
        address = address.toLocalizedSafeString(languageCode),
        services = services.toLocalizedSafeString(languageCode),
        openStatus = availability.toLocalizedSafeString(languageCode),
        phone = contact
    )
}

fun HealthDto.toGenericModel(languageCode: String): DoctorModel { // For ambulance/police if using same model
    return DoctorModel(
        id = id,
        name = name.toLocalizedSafeString(languageCode),
        address = address.toLocalizedSafeString(languageCode),
        specialization = type,
        availability = availability.toLocalizedSafeString(languageCode),
        phone = contact
    )
}
