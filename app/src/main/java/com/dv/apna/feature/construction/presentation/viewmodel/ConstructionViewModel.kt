package com.dv.apna.feature.construction.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dv.apna.feature.construction.presentation.effect.ConstructionEffect
import com.dv.apna.feature.construction.presentation.event.ConstructionEvent
import com.dv.apna.feature.construction.presentation.state.ConstructionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConstructionViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(ConstructionState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ConstructionEffect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: ConstructionEvent) {
        when (event) {
            is ConstructionEvent.BackClick -> {
                viewModelScope.launch {
                    _effect.emit(ConstructionEffect.NavigateBack)
                }
            }
            is ConstructionEvent.BricksClick -> {
                viewModelScope.launch {
                    _effect.emit(ConstructionEffect.NavigateToBricks)
                }
            }
            is ConstructionEvent.MaterialShopsClick -> {
                viewModelScope.launch {
                    _effect.emit(ConstructionEffect.NavigateToMaterialShops)
                }
            }
            is ConstructionEvent.HardwareShopsClick -> {
                viewModelScope.launch {
                    _effect.emit(ConstructionEffect.NavigateToHardwareShops)
                }
            }
        }
    }
}
