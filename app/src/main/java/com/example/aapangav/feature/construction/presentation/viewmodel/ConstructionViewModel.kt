package com.example.aapangav.feature.construction.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.aapangav.feature.construction.presentation.state.ConstructionState
import com.example.aapangav.feature.construction.presentation.event.ConstructionEvent
import com.example.aapangav.feature.construction.presentation.effect.ConstructionEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ConstructionViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(ConstructionState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ConstructionEffect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: ConstructionEvent) {
        // Handle events
    }
}