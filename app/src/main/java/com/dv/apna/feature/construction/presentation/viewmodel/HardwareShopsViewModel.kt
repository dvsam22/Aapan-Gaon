package com.dv.apna.feature.construction.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dv.apna.core.common.Resource
import com.dv.apna.core.datastore.PreferenceManager
import com.dv.apna.feature.construction.domain.usecase.GetHardwareShopsUseCase
import com.dv.apna.feature.construction.presentation.effect.HardwareShopsEffect
import com.dv.apna.feature.construction.presentation.event.HardwareShopsEvent
import com.dv.apna.feature.construction.presentation.state.HardwareShopsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HardwareShopsViewModel @Inject constructor(
    private val getHardwareShopsUseCase: GetHardwareShopsUseCase,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _state = MutableStateFlow(HardwareShopsState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<HardwareShopsEffect>()
    val effect = _effect.asSharedFlow()

    init {
        loadShops()
    }

    private fun loadShops() {
        viewModelScope.launch {
            val villageId = preferenceManager.villageId.firstOrNull()
            if (villageId != null) {
                getHardwareShopsUseCase(villageId).onEach { result ->
                    when (result) {
                        is Resource.Success<*> -> {
                            _state.update {
                                it.copy(
                                    shops = result.data as? List<com.dv.apna.feature.construction.domain.model.HardwareShopModel> ?: emptyList(),
                                    isLoading = false
                                )
                            }
                        }
                        is Resource.Error<*> -> {
                            _state.update { it.copy(error = result.message, isLoading = false) }
                        }
                        is Resource.Loading<*> -> {
                            _state.update { it.copy(isLoading = true) }
                        }
                    }
                }.launchIn(viewModelScope)
            } else {
                _state.update { it.copy(error = "Village not selected", isLoading = false) }
            }
        }
    }

    fun onEvent(event: HardwareShopsEvent) {
        when (event) {
            HardwareShopsEvent.BackClick -> {
                viewModelScope.launch { _effect.emit(HardwareShopsEffect.NavigateBack) }
            }
            is HardwareShopsEvent.CallClick -> {
                viewModelScope.launch { _effect.emit(HardwareShopsEffect.DialPhone(event.phone)) }
            }
        }
    }
}
