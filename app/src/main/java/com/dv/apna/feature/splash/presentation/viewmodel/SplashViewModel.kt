package com.dv.apna.feature.splash.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dv.apna.feature.splash.presentation.state.SplashState
import com.dv.apna.feature.splash.presentation.event.SplashEvent
import com.dv.apna.feature.splash.presentation.effect.SplashEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(SplashState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SplashEffect>()
    val effect = _effect.asSharedFlow()

    init {
        startTimer()
    }

    private fun startTimer() {
        viewModelScope.launch {
            delay(2000)
            _effect.emit(SplashEffect.NavigateToNext)
        }
    }

    fun onEvent(event: SplashEvent) {
        // Handle events if any
    }
}
