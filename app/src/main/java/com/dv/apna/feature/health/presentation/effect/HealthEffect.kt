package com.dv.apna.feature.health.presentation.effect

sealed interface HealthEffect {
    data object NavigateBack : HealthEffect
    data class DialPhone(val phone: String) : HealthEffect
    data object NavigateToDoctors : HealthEffect
    data object NavigateToHospitals : HealthEffect
    data object NavigateToPharmacy : HealthEffect
    data object NavigateToAmbulance : HealthEffect
    data object NavigateToPolice : HealthEffect
}
