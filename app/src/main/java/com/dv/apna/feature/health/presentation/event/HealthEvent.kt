package com.dv.apna.feature.health.presentation.event

import com.dv.apna.feature.health.domain.model.DoctorModel
import com.dv.apna.feature.health.domain.model.HospitalModel
import com.dv.apna.feature.health.domain.model.PharmacyModel

sealed interface HealthEvent {
    data object BackClick : HealthEvent
    data class CallAmbulance(val phone: String) : HealthEvent
    data class CallPolice(val phone: String) : HealthEvent
    data class CallSarpanch(val phone: String) : HealthEvent
    data object DoctorsClick : HealthEvent
    data object HospitalsClick : HealthEvent
    data object PharmacyClick : HealthEvent
    data object AmbulanceClick : HealthEvent
    data object PoliceClick : HealthEvent
    data class DoctorClick(val doctor: DoctorModel) : HealthEvent
    data class HospitalClick(val hospital: HospitalModel) : HealthEvent
    data class PharmacyClickDetail(val pharmacy: PharmacyModel) : HealthEvent
    data class CallClick(val phone: String) : HealthEvent
}
