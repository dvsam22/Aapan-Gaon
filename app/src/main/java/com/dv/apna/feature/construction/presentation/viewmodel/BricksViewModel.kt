package com.dv.apna.feature.construction.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dv.apna.feature.construction.domain.model.BrickTypePrice
import com.dv.apna.feature.construction.domain.model.BricksSupplierModel
import com.dv.apna.feature.construction.presentation.effect.BricksEffect
import com.dv.apna.feature.construction.presentation.event.BricksEvent
import com.dv.apna.feature.construction.presentation.state.BricksState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BricksViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(BricksState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<BricksEffect>()
    val effect = _effect.asSharedFlow()

    init {
        loadSuppliers()
    }

    private fun loadSuppliers() {
        // Dummy data based on design
        val dummySuppliers = listOf(
            BricksSupplierModel(
                id = "1",
                name = "Shree Balaji Bricks",
                address = "Rampur Village (Near Middle School)",
                brickTypes = listOf(
                    BrickTypePrice("Red Clay", "₹8 / Brick"),
                    BrickTypePrice("Fly Ash", "₹9 / Brick"),
                    BrickTypePrice("Concrete Blocks", "₹12 / Brick"),
                    BrickTypePrice("Hollow Blocks", "₹10 / Brick")
                )
            ),
            BricksSupplierModel(
                id = "2",
                name = "Mahadev Bricks & Co.",
                address = "Rampur Village (Near Middle School)",
                brickTypes = listOf(
                    BrickTypePrice("Red Clay", "₹8 / Brick"),
                    BrickTypePrice("Fly Ash", "₹9 / Brick"),
                    BrickTypePrice("Concrete Blocks", "₹12 / Brick"),
                    BrickTypePrice("Hollow Blocks", "₹10 / Brick")
                )
            ),
            BricksSupplierModel(
                id = "3",
                name = "Sri Lakshmi Brick Works",
                address = "Rampur Village (Near Middle School)",
                brickTypes = listOf(
                    BrickTypePrice("Red Clay", "₹8 / Brick"),
                    BrickTypePrice("Fly Ash", "₹9 / Brick"),
                    BrickTypePrice("Concrete Blocks", "₹12 / Brick"),
                    BrickTypePrice("Hollow Blocks", "₹10 / Brick")
                )
            )
        )
        _state.update { it.copy(suppliers = dummySuppliers) }
    }

    fun onEvent(event: BricksEvent) {
        when (event) {
            is BricksEvent.BackClick -> {
                viewModelScope.launch { _effect.emit(BricksEffect.NavigateBack) }
            }
            is BricksEvent.CallClick -> {
                viewModelScope.launch { _effect.emit(BricksEffect.DialPhone(event.phone)) }
            }
        }
    }
}
