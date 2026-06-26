package com.dv.apna.feature.notification.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.dv.apna.feature.notification.presentation.state.NotificationState
import com.dv.apna.feature.notification.presentation.event.NotificationEvent
import com.dv.apna.feature.notification.presentation.effect.NotificationEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(NotificationState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<NotificationEffect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: NotificationEvent) {
        // Handle events
    }
}