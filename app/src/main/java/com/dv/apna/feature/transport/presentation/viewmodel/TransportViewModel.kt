package com.dv.apna.feature.transport.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dv.apna.R
import com.dv.apna.core.common.Resource
import com.dv.apna.core.common.UiText
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
        if (categoryId != null) {
            val services = listOf(
                TransportService(UiText.StringResource(R.string.tractor), R.drawable.transport, "tractor"),
                TransportService(UiText.StringResource(R.string.car), R.drawable.iv_car, "car"),
                TransportService(UiText.StringResource(R.string.pickup), R.drawable.pickup, "pickup"),
                TransportService(UiText.StringResource(R.string.loader), R.drawable.ic_loader, "loader"),
                TransportService(UiText.StringResource(R.string.jcb), R.drawable.ic_jcb, "jcb")
            )
            val service = services.find { it.categoryId == categoryId }
            _state.update { it.copy(
                selectedCategory = categoryId,
                selectedCategoryTitle = service?.title ?: UiText.DynamicString(categoryId)
            ) }
            getTransportDetails(categoryId)
        }
    }

    private fun getTransportServices() {
        val services = listOf(
            TransportService(UiText.StringResource(R.string.tractor), R.drawable.transport, "tractor"),
            TransportService(UiText.StringResource(R.string.car), R.drawable.iv_car, "car"),
            TransportService(UiText.StringResource(R.string.pickup), R.drawable.pickup, "pickup"),
            TransportService(UiText.StringResource(R.string.loader), R.drawable.ic_loader, "loader"),
            TransportService(UiText.StringResource(R.string.jcb), R.drawable.ic_jcb, "jcb")
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
                            _state.update { it.copy(error = UiText.DynamicString(result.message ?: "Unknown error"), isLoading = false) }
                        }
                        is Resource.Loading -> {
                            _state.update { it.copy(isLoading = true) }
                        }
                    }
                }.launchIn(viewModelScope)
            } else {
                _state.update { it.copy(error = UiText.StringResource(R.string.error_village_not_selected), isLoading = false) }
            }
        }
    }
}
