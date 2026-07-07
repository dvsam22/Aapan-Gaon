package com.dv.apna.feature.mandi.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dv.apna.core.common.Resource
import com.dv.apna.core.datastore.PreferenceManager
import com.dv.apna.feature.mandi.domain.usecase.GetCropPricesUseCase
import com.dv.apna.feature.mandi.domain.usecase.GetLocalBuyersUseCase
import com.dv.apna.feature.mandi.domain.usecase.GetMarketPricesUseCase
import com.dv.apna.feature.mandi.presentation.effect.MandiEffect
import com.dv.apna.feature.mandi.presentation.event.MandiEvent
import com.dv.apna.feature.mandi.presentation.state.MandiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MandiViewModel @Inject constructor(
    private val getCropPricesUseCase: GetCropPricesUseCase,
    private val getMarketPricesUseCase: GetMarketPricesUseCase,
    private val getLocalBuyersUseCase: GetLocalBuyersUseCase,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _state = MutableStateFlow(MandiState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<MandiEffect>()
    val effect = _effect.asSharedFlow()

    init {
        loadAllMandiData()
    }

    private fun loadAllMandiData() {
        viewModelScope.launch {
            val villageId = preferenceManager.villageId.firstOrNull()
            if (villageId != null) {
                loadCropPrices(villageId)
                loadMarketPrices(villageId)
                loadLocalBuyers(villageId)
            } else {
                _state.update { it.copy(error = "Village not selected") }
            }
        }
    }

    private fun loadCropPrices(villageId: String) {
        getCropPricesUseCase(villageId).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.update { it.copy(cropPrices = result.data ?: emptyList(), isLoading = false) }
                }
                is Resource.Error -> {
                    _state.update { it.copy(error = result.message, isLoading = false) }
                }
                is Resource.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun loadMarketPrices(villageId: String) {
        getMarketPricesUseCase(villageId).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.update { it.copy(marketPrices = result.data ?: emptyList(), isLoading = false) }
                }
                is Resource.Error -> {
                    _state.update { it.copy(error = result.message, isLoading = false) }
                }
                is Resource.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun loadLocalBuyers(villageId: String) {
        getLocalBuyersUseCase(villageId).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.update { it.copy(localBuyers = result.data ?: emptyList(), isLoading = false) }
                }
                is Resource.Error -> {
                    _state.update { it.copy(error = result.message, isLoading = false) }
                }
                is Resource.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun onEvent(event: MandiEvent) {
        when (event) {
            is MandiEvent.BackClick -> {
                viewModelScope.launch {
                    _effect.emit(MandiEffect.NavigateBack)
                }
            }
            is MandiEvent.CropPricesClick -> {
                viewModelScope.launch {
                    _effect.emit(MandiEffect.NavigateToCropPrices)
                }
            }
            is MandiEvent.TodayMarketClick -> {
                viewModelScope.launch {
                    _effect.emit(MandiEffect.NavigateToTodayMarket)
                }
            }
            is MandiEvent.LocalBuyersClick -> {
                viewModelScope.launch {
                    _effect.emit(MandiEffect.NavigateToLocalBuyers)
                }
            }
            is MandiEvent.CallClick -> {
                viewModelScope.launch {
                    _effect.emit(MandiEffect.DialPhone(event.phone))
                }
            }
        }
    }
}
