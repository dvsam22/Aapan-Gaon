package com.dv.apna.feature.notification.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dv.apna.core.common.Resource
import com.dv.apna.core.common.UiText
import com.dv.apna.core.datastore.PreferenceManager
import com.dv.apna.feature.notification.domain.usecase.GetNotificationDataUseCase
import com.dv.apna.feature.notification.presentation.effect.NotificationEffect
import com.dv.apna.feature.notification.presentation.event.NotificationEvent
import com.dv.apna.feature.notification.presentation.state.NotificationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val getNotificationDataUseCase: GetNotificationDataUseCase,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<NotificationEffect>()
    val effect = _effect.asSharedFlow()

    private var loadJob: Job? = null

    init {
        loadNotifications()
    }

    fun onEvent(event: NotificationEvent) {
        when (event) {
            is NotificationEvent.Refresh -> loadNotifications()
            is NotificationEvent.BackClick -> {
                viewModelScope.launch { _effect.emit(NotificationEffect.NavigateBack) }
            }
            is NotificationEvent.SelectNotification -> {
                viewModelScope.launch {
                    _effect.emit(NotificationEffect.NavigateToDetails(event.notificationId))
                }
            }
        }
    }

    private fun loadNotifications() {
        loadJob?.cancel()
        loadJob = combine(
            preferenceManager.villageId.filterNotNull(),
            preferenceManager.languageCode
        ) { villageId, _ ->
            villageId
        }.flatMapLatest { villageId ->
                getNotificationDataUseCase(villageId)
            }
            .onEach { result ->
                when (result) {
                    is Resource.Success<*> -> {
                        _state.update {
                            it.copy(
                                notifications = result.data as? List<com.dv.apna.feature.notification.domain.model.NotificationModel>
                                    ?: emptyList(),
                                isLoading = false
                            )
                        }
                    }

                    is Resource.Error<*> -> {
                        _state.update { it.copy(error = UiText.DynamicString(result.message ?: "Unknown error"), isLoading = false) }
                    }

                    is Resource.Loading<*> -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                }
            }.launchIn(viewModelScope)
    }
}
