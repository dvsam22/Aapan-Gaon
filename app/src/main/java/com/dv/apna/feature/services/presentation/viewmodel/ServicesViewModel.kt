package com.dv.apna.feature.services.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.dv.apna.feature.services.presentation.state.ServicesState
import com.dv.apna.feature.services.presentation.event.ServicesEvent
import com.dv.apna.feature.services.presentation.effect.ServicesEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ServicesViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(ServicesState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ServicesEffect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: ServicesEvent) {
        // Handle events
    }
}