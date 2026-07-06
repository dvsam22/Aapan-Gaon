package com.dv.apna.feature.notification.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dv.apna.feature.notification.domain.model.NotificationModel
import com.dv.apna.feature.notification.presentation.effect.NotificationEffect
import com.dv.apna.feature.notification.presentation.event.NotificationEvent
import com.dv.apna.feature.notification.presentation.state.NotificationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(NotificationState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<NotificationEffect>()
    val effect = _effect.asSharedFlow()

    init {
        loadNotifications()
    }

    fun onEvent(event: NotificationEvent) {
        when (event) {
            is NotificationEvent.Refresh -> loadNotifications()
            is NotificationEvent.SelectNotification -> {
                viewModelScope.launch {
                    _effect.emit(NotificationEffect.NavigateToDetails(event.notificationId))
                }
            }
        }
    }

    private fun loadNotifications() {
        val dummyNotifications = listOf(
            NotificationModel(
                id = "1",
                title = "Government increases MSP for Kharif crops",
                summary = "The government has announced an....",
                description = "The Government of India has approved an increase in the Minimum Support Price (MSP) for major Kharif crops for the upcoming sowing season. The revised MSP aims to provide better income to farmers while encouraging the cultivation of essential food grains and oilseeds.\n\nAccording to the official announcement, crops including Paddy, Maize, Jowar, Bajra, Ragi, Tur (Arhar), Moong, Urad, Groundnut, Soybean, Sunflower, Sesamum, Cotton, and Nigerseed will receive higher procurement prices than the previous season.\n\nThe government stated that the new MSP has been determined to ensure farmers receive a fair return over their cost of cultivation. Procurement agencies have also been instructed to make necessary arrangements for purchasing eligible crops directly from farmers during the harvest season.\n\nFarmers are advised to verify the revised MSP rates at their nearest Agriculture Office, Krishi Vigyan Kendra (KVK), or Procurement Centre before selling their produce.",
                time = "2 Hr ago",
                date = "Today, 01:30PM",
                category = "Today"
            ),
            NotificationModel(
                id = "2",
                title = "Government increases MSP for Kharif crops",
                summary = "The government has announced an....",
                description = "Detailed description here...",
                time = "4 Hr ago",
                date = "Today, 11:30AM",
                category = "Today"
            ),
            NotificationModel(
                id = "3",
                title = "Government increases MSP for Kharif crops",
                summary = "The government has announced an....",
                description = "Detailed description here...",
                time = "6 Hr ago",
                date = "Today, 09:30AM",
                category = "Today"
            ),
            NotificationModel(
                id = "4",
                title = "Government increases MSP for Kharif crops",
                summary = "The government has announced an....",
                description = "Detailed description here...",
                time = "Yesterday",
                date = "Yesterday, 02:30PM",
                category = "Yesterday"
            ),
            NotificationModel(
                id = "5",
                title = "Government increases MSP for Kharif crops",
                summary = "The government has announced an....",
                description = "Detailed description here...",
                time = "Yesterday",
                date = "Yesterday, 10:30AM",
                category = "Yesterday"
            ),
            NotificationModel(
                id = "6",
                title = "Government increases MSP for Kharif crops",
                summary = "The government has announced an....",
                description = "Detailed description here...",
                time = "Yesterday",
                date = "Yesterday, 08:30AM",
                category = "Yesterday"
            )
            ,
            NotificationModel(
                id = "6",
                title = "Government increases MSP for Kharif crops",
                summary = "The government has announced an....",
                description = "Detailed description here...",
                time = "Yesterday",
                date = "Yesterday, 08:30AM",
                category = "Yesterday"
            )
            ,
            NotificationModel(
                id = "6",
                title = "Government increases MSP for Kharif crops",
                summary = "The government has announced an....",
                description = "Detailed description here...",
                time = "Yesterday",
                date = "Yesterday, 08:30AM",
                category = "Yesterday"
            )
        )
        _state.update { it.copy(notifications = dummyNotifications) }
    }
}
