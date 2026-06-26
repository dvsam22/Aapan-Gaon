package com.dv.apna.feature.transport.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.dv.apna.feature.transport.presentation.state.TransportState
import com.dv.apna.feature.transport.presentation.event.TransportEvent
import com.dv.apna.feature.transport.presentation.effect.TransportEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class TransportViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(TransportState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<TransportEffect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: TransportEvent) {
        // Handle events
    }
}