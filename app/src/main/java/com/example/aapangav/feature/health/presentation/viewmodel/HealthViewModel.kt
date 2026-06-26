package com.example.aapangav.feature.health.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.aapangav.feature.health.presentation.state.HealthState
import com.example.aapangav.feature.health.presentation.event.HealthEvent
import com.example.aapangav.feature.health.presentation.effect.HealthEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HealthViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(HealthState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<HealthEffect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: HealthEvent) {
        // Handle events
    }
}