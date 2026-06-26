package com.dv.apna.feature.settings.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.dv.apna.feature.settings.presentation.state.SettingsState
import com.dv.apna.feature.settings.presentation.event.SettingsEvent
import com.dv.apna.feature.settings.presentation.effect.SettingsEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SettingsEffect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: SettingsEvent) {
        // Handle events
    }
}