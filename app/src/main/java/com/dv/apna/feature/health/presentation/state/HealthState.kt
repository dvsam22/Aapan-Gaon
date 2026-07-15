package com.dv.apna.feature.health.presentation.state

import com.dv.apna.core.common.UiText
import com.dv.apna.feature.health.domain.model.DoctorModel
import com.dv.apna.feature.health.domain.model.HospitalModel
import com.dv.apna.feature.health.domain.model.PharmacyModel

data class HealthState(
    val isLoading: Boolean = false,
    val doctors: List<DoctorModel> = emptyList(),
    val hospitals: List<HospitalModel> = emptyList(),
    val pharmacies: List<PharmacyModel> = emptyList(),
    val ambulances: List<DoctorModel> = emptyList(),
    val police: List<DoctorModel> = emptyList(),
    val sarpanchName: String = "",
    val sarpanchPhone: String = "",
    val error: UiText? = null
)
