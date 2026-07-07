package com.dv.apna.feature.transport.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dv.apna.R
import com.dv.apna.core.common.Resource
import com.dv.apna.core.datastore.PreferenceManager
import com.dv.apna.feature.transport.domain.model.TransportService
import com.dv.apna.feature.transport.domain.usecase.GetTransportByCategoryUseCase
import com.dv.apna.feature.transport.presentation.effect.TransportEffect
import com.dv.apna.feature.transport.presentation.event.TransportEvent
import com.dv.apna.feature.transport.presentation.state.TransportState
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
class TransportViewModel @Inject constructor(
    private val getTransportByCategoryUseCase: GetTransportByCategoryUseCase,
    private val preferenceManager: PreferenceManager,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(TransportState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<TransportEffect>()
    val effect = _effect.asSharedFlow()

    init {
        getTransportServices()
        checkTransportDetails()
    }

    private fun checkTransportDetails() {
        val categoryId = savedStateHandle.get<String>("categoryId")
        val categoryName = savedStateHandle.get<String>("categoryName")
        if (categoryId != null && categoryName != null) {
            _state.update { it.copy(selectedCategory = categoryName) }
            getTransportDetails(categoryId)
        }
    }

    private fun getTransportServices() {
        val services = listOf(
            TransportService("Tractor", R.drawable.transport, "tractor"),
            TransportService("Car", R.drawable.iv_car, "car"),
            TransportService("Pickup", R.drawable.pickup, "pickup"),
            TransportService("Loader", R.drawable.transport, "loader"),
            TransportService("JCB", R.drawable.transport, "jcb")
        )
        _state.update { it.copy(services = services) }
    }

    fun onEvent(event: TransportEvent) {
        when (event) {
            is TransportEvent.BackClick -> {
                viewModelScope.launch { _effect.emit(TransportEffect.NavigateBack) }
            }

            is TransportEvent.CategoryClick -> {
                viewModelScope.launch { 
                    _effect.emit(TransportEffect.NavigateToCategory(event.categoryId, event.categoryName)) 
                }
            }
            is TransportEvent.CallClick -> {
                viewModelScope.launch { _effect.emit(TransportEffect.DialPhone(event.contact)) }
            }
        }
    }

    private fun getTransportDetails(categoryId: String) {
        viewModelScope.launch {
            val villageId = preferenceManager.villageId.firstOrNull()
            if (villageId != null) {
                getTransportByCategoryUseCase(villageId, categoryId).onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            _state.update { it.copy(transportDetails = result.data ?: emptyList(), isLoading = false) }
                        }
                        is Resource.Error -> {
                            _state.update { it.copy(error = result.message, isLoading = false) }
                        }
                        is Resource.Loading -> {
                            _state.update { it.copy(isLoading = true) }
                        }
                    }
                }.launchIn(viewModelScope)
            } else {
                _state.update { it.copy(error = "Village not selected", isLoading = false) }
            }
        }
    }
}
