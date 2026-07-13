package com.dv.apna.feature.splash.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dv.apna.core.datastore.PreferenceManager
import com.dv.apna.feature.splash.presentation.effect.SplashEffect
import com.dv.apna.feature.splash.presentation.state.SplashState
import com.dv.apna.feature.splash.presentation.event.SplashEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _state = MutableStateFlow(SplashState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SplashEffect>()
    val effect = _effect.asSharedFlow()

    init {
        checkNavigation()
    }

    private fun checkNavigation() {
        viewModelScope.launch {
            delay(800)
            val villageId = preferenceManager.villageId.first()
            if (villageId != null) {
                _effect.emit(SplashEffect.NavigateToHome)
            } else {
                _effect.emit(SplashEffect.NavigateToLanguage)
            }
        }
    }

    fun onEvent(event: SplashEvent) {
    }
}
