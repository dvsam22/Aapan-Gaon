package com.dv.apna.feature.health.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dv.apna.core.common.Resource
import com.dv.apna.core.datastore.PreferenceManager
import com.dv.apna.feature.health.domain.usecase.*
import com.dv.apna.feature.health.presentation.effect.HealthEffect
import com.dv.apna.feature.health.presentation.event.HealthEvent
import com.dv.apna.feature.health.presentation.state.HealthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HealthViewModel @Inject constructor(
    private val getDoctorsUseCase: GetDoctorsUseCase,
    private val getHospitalsUseCase: GetHospitalsUseCase,
    private val getPharmaciesUseCase: GetPharmaciesUseCase,
    private val getAmbulancesUseCase: GetAmbulancesUseCase,
    private val getPoliceUseCase: GetPoliceUseCase,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _state = MutableStateFlow(HealthState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<HealthEffect>()
    val effect = _effect.asSharedFlow()

    init {
        loadHealthData()
    }

    private fun loadHealthData() {
        viewModelScope.launch {
            val villageId = preferenceManager.villageId.firstOrNull()
            if (villageId != null) {
                fetchDoctors(villageId)
                fetchHospitals(villageId)
                fetchPharmacies(villageId)
                fetchAmbulances(villageId)
                fetchPolice(villageId)
            } else {
                _state.update { it.copy(error = "Village not selected") }
            }
        }
    }

    private fun fetchDoctors(villageId: String) {
        getDoctorsUseCase(villageId).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.update { it.copy(doctors = result.data ?: emptyList(), isLoading = false) }
                }
                is Resource.Error -> {
                    _state.update { it.copy(error = result.message, isLoading = false) }
                }
                is Resource.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun fetchHospitals(villageId: String) {
        getHospitalsUseCase(villageId).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.update { it.copy(hospitals = result.data ?: emptyList(), isLoading = false) }
                }
                is Resource.Error -> {
                    _state.update { it.copy(error = result.message, isLoading = false) }
                }
                is Resource.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun fetchPharmacies(villageId: String) {
        getPharmaciesUseCase(villageId).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.update { it.copy(pharmacies = result.data ?: emptyList(), isLoading = false) }
                }
                is Resource.Error -> {
                    _state.update { it.copy(error = result.message, isLoading = false) }
                }
                is Resource.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun fetchAmbulances(villageId: String) {
        getAmbulancesUseCase(villageId).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.update { it.copy(ambulances = result.data ?: emptyList(), isLoading = false) }
                }
                is Resource.Error -> {
                    _state.update { it.copy(error = result.message, isLoading = false) }
                }
                is Resource.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun fetchPolice(villageId: String) {
        getPoliceUseCase(villageId).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.update { it.copy(police = result.data ?: emptyList(), isLoading = false) }
                }
                is Resource.Error -> {
                    _state.update { it.copy(error = result.message, isLoading = false) }
                }
                is Resource.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }
            }
        }.launchIn(viewModelScope)
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
            HealthEvent.AmbulanceClick -> {
                viewModelScope.launch { _effect.emit(HealthEffect.NavigateToAmbulance) }
            }
            HealthEvent.PoliceClick -> {
                viewModelScope.launch { _effect.emit(HealthEffect.NavigateToPolice) }
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
