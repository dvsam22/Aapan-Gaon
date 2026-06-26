package com.example.aapangav.feature.mandi.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.aapangav.feature.mandi.presentation.state.MandiState
import com.example.aapangav.feature.mandi.presentation.event.MandiEvent
import com.example.aapangav.feature.mandi.presentation.effect.MandiEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MandiViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(MandiState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<MandiEffect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: MandiEvent) {
        // Handle events
    }
}