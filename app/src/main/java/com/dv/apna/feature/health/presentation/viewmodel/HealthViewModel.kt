package com.dv.apna.feature.health.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dv.apna.feature.health.domain.model.DoctorModel
import com.dv.apna.feature.health.domain.model.HospitalModel
import com.dv.apna.feature.health.domain.model.PharmacyModel
import com.dv.apna.feature.health.presentation.effect.HealthEffect
import com.dv.apna.feature.health.presentation.event.HealthEvent
import com.dv.apna.feature.health.presentation.state.HealthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HealthViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(HealthState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<HealthEffect>()
    val effect = _effect.asSharedFlow()

    init {
        loadData()
    }

    private fun loadData() {
        val dummyDoctors = listOf(
            DoctorModel("1", "Dr. Anil Sharma", "Rampur Village (Near Middle School)", "General Physician", "09:30AM - 04:00PM", "1234567890"),
            DoctorModel("2", "Dr. Priya Verma", "Rampur Village (Near Middle School)", "Gynaecologist", "10:00AM - 05:00PM", "1234567890"),
            DoctorModel("3", "Dr. Rahul Mehta", "Rampur Village (Near Middle School)", "Paediatrician", "10:00AM - 05:00PM", "1234567890")
        )

        val dummyHospitals = listOf(
            HospitalModel("1", "Rampur Community Hospital", "Rampur Village (Near Middle School)", "Multi Speciality Hospital", "OPD, Emergency, Pharmacy, Lab, ICU", "Open: 24 Hours", "1234567890"),
            HospitalModel("2", "Sharma Hospitals", "Rampur Village (Near Middle School)", "Multi Speciality Hospital", "OPD, Emergency, Pharmacy, Lab, ICU", "Open: 24 Hours", "1234567890"),
            HospitalModel("3", "Life Care Hospitals", "Rampur Village (Near Middle School)", "Multi Speciality Hospital", "OPD, Emergency, Pharmacy, Lab, ICU", "Open: 24 Hours", "1234567890")
        )

        val dummyPharmacies = listOf(
            PharmacyModel("1", "Sharma Medical Store", "Rampur Village (Near Middle School)", "All Medicines Available", "Open: 08:00AM - 10:00PM", "1234567890"),
            PharmacyModel("2", "Life Care Pharmacy", "Rampur Village (Near Middle School)", "All Medicines Available", "Open: 24 Hours", "1234567890"),
            PharmacyModel("3", "MedPlus Pharmacy", "Rampur Village (Near Middle School)", "All Medicines Available", "Open: 08:00AM - 11:00PM", "1234567890")
        )

        _state.update { 
            it.copy(
                doctors = dummyDoctors,
                hospitals = dummyHospitals,
                pharmacies = dummyPharmacies
            ) 
        }
    }

    fun onEvent(event: HealthEvent) {
        when (event) {
            HealthEvent.BackClick -> {
                viewModelScope.launch { _effect.emit(HealthEffect.NavigateBack) }
            }
            is HealthEvent.CallAmbulance -> {
                viewModelScope.launch { _effect.emit(HealthEffect.DialPhone(event.phone)) }
            }
            is HealthEvent.CallPolice -> {
                viewModelScope.launch { _effect.emit(HealthEffect.DialPhone(event.phone)) }
            }
            HealthEvent.DoctorsClick -> {
                viewModelScope.launch { _effect.emit(HealthEffect.NavigateToDoctors) }
            }
            HealthEvent.HospitalsClick -> {
                viewModelScope.launch { _effect.emit(HealthEffect.NavigateToHospitals) }
            }
            HealthEvent.PharmacyClick -> {
                viewModelScope.launch { _effect.emit(HealthEffect.NavigateToPharmacy) }
            }
            is HealthEvent.CallClick -> {
                viewModelScope.launch { _effect.emit(HealthEffect.DialPhone(event.phone)) }
            }
            is HealthEvent.DoctorClick -> {}
            is HealthEvent.HospitalClick -> {}
            is HealthEvent.PharmacyClickDetail -> {}
        }
    }
}
