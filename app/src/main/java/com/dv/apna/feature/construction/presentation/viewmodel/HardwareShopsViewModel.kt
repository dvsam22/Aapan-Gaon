package com.dv.apna.feature.construction.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dv.apna.feature.construction.domain.model.HardwareShopModel
import com.dv.apna.feature.construction.domain.model.HardwareItemPrice
import com.dv.apna.feature.construction.presentation.effect.HardwareShopsEffect
import com.dv.apna.feature.construction.presentation.event.HardwareShopsEvent
import com.dv.apna.feature.construction.presentation.state.HardwareShopsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HardwareShopsViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(HardwareShopsState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<HardwareShopsEffect>()
    val effect = _effect.asSharedFlow()

    init {
        loadShops()
    }

    private fun loadShops() {
        val dummyShops = listOf(
            HardwareShopModel(
                id = "1",
                name = "Mahalaxmi Hardware",
                address = "Main Market, Rampur",
                items = listOf(
                    HardwareItemPrice("Hammer", "₹250"),
                    HardwareItemPrice("Drill Machine", "₹2,500"),
                    HardwareItemPrice("Nails (1kg)", "₹120"),
                    HardwareItemPrice("Screwdriver Set", "₹450")
                )
            ),
            HardwareShopModel(
                id = "2",
                name = "Janta Hardware Store",
                address = "Near Bus Stand, Rampur",
                items = listOf(
                    HardwareItemPrice("Paint (White, 10L)", "₹1,800"),
                    HardwareItemPrice("Brushes", "₹60 / pc"),
                    HardwareItemPrice("Pipe Fittings", "₹40 / pc"),
                    HardwareItemPrice("Door Locks", "₹350")
                )
            ),
            HardwareShopModel(
                id = "3",
                name = "Singh Hardware & Tools",
                address = "Station Road, Rampur",
                items = listOf(
                    HardwareItemPrice("Power Saw", "₹4,200"),
                    HardwareItemPrice("Measuring Tape", "₹150"),
                    HardwareItemPrice("Glue (1kg)", "₹300"),
                    HardwareItemPrice("Wrench Set", "₹850")
                )
            )
        )
        _state.update { it.copy(shops = dummyShops) }
    }

    fun onEvent(event: HardwareShopsEvent) {
        when (event) {
            is HardwareShopsEvent.BackClick -> {
                viewModelScope.launch { _effect.emit(HardwareShopsEffect.NavigateBack) }
            }
            is HardwareShopsEvent.CallClick -> {
                viewModelScope.launch { _effect.emit(HardwareShopsEffect.DialPhone(event.phone)) }
            }
        }
    }
}
