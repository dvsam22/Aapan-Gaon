package com.dv.apna.feature.transport.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.dv.apna.R
import com.dv.apna.core.navigation.Route
import com.dv.apna.feature.transport.domain.model.TransportDetails
import com.dv.apna.feature.transport.domain.model.TransportService
import com.dv.apna.feature.transport.presentation.effect.TransportEffect
import com.dv.apna.feature.transport.presentation.event.TransportEvent
import com.dv.apna.feature.transport.presentation.state.TransportState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransportViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(TransportState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<TransportEffect>()
    val effect = _effect.asSharedFlow()

    init {
        getTransportServices()
        checkTransportDetails()
    }

    private fun checkTransportDetails() {
        try {
            val route = savedStateHandle.toRoute<Route.TransportDetails>()
            _state.update { it.copy(selectedCategory = route.category) }
            getTransportDetails(route.category)
        } catch (e: Exception) {
            // Not in TransportDetails route
        }
    }

    private fun getTransportServices() {
        val services = listOf(
            TransportService("Tractor", R.drawable.transport),
            TransportService("Car", R.drawable.iv_car),
            TransportService("Pickup", R.drawable.pickup),
            TransportService("Loader", R.drawable.iv_car),
            TransportService("JCB", R.drawable.iv_car)

        )
        _state.update { it.copy(services = services) }
    }

    fun onEvent(event: TransportEvent) {
        when (event) {
            is TransportEvent.BackClick -> {
                viewModelScope.launch { _effect.emit(TransportEffect.NavigateBack) }
            }

            is TransportEvent.CategoryClick -> {
                _state.update { it.copy(selectedCategory = event.category) }
                getTransportDetails(event.category)
                viewModelScope.launch { _effect.emit(TransportEffect.NavigateToCategory(event.category)) }
            }
        }
    }

    private fun getTransportDetails(category: String) {
        // Mock data
        val details = listOf(
            TransportDetails(
                name = "Sohan Singh",
                address = "Rampur Village (Near Middle School)",
                vehicleType = "$category - Mahindra 575",
                charges = "₹800 / Hour",
                phoneNumber = "1234567890"
            ), TransportDetails(
                name = "Madan Lal",
                address = "Rampur Village (Near Middle School)",
                vehicleType = "$category - Swaraj 744",
                charges = "₹750 / Hour",
                phoneNumber = "1234567890"
            )
        )
        _state.update { it.copy(transportDetails = details) }
    }
}
