package com.dv.apna.feature.splash.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.dv.apna.core.datastore.PreferenceManager
import com.dv.apna.core.navigation.Route
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
    private val preferenceManager: PreferenceManager,
    private val savedStateHandle: SavedStateHandle
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
            // Wait for animations to complete (1200ms) + 1 second additional delay
            delay(2200)
            val villageId = preferenceManager.villageId.first()
            
            // Extract from type-safe Route arguments
            val splashRoute = try { 
                savedStateHandle.toRoute<Route.Splash>() 
            } catch (e: Exception) { 
                null 
            }
            
            val notificationId = splashRoute?.notificationId
            val notificationType = splashRoute?.notificationType

            Log.d("FCM_DEBUG", "Splash detected notification from Route: id=$notificationId, type=$notificationType")

            if (villageId != null) {
                if (!notificationId.isNullOrBlank()) {
                    _effect.emit(SplashEffect.NavigateToNotificationDetails(notificationId, notificationType))
                } else {
                    _effect.emit(SplashEffect.NavigateToHome)
                }
            } else {
                _effect.emit(SplashEffect.NavigateToLanguage)
            }
        }
    }

    fun onEvent(event: SplashEvent) {}
}
