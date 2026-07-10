package com.dv.apna.feature.construction.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dv.apna.core.common.Resource
import com.dv.apna.core.datastore.PreferenceManager
import com.dv.apna.feature.construction.domain.usecase.GetMaterialShopsUseCase
import com.dv.apna.feature.construction.presentation.effect.MaterialShopsEffect
import com.dv.apna.feature.construction.presentation.event.MaterialShopsEvent
import com.dv.apna.feature.construction.presentation.state.MaterialShopsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MaterialShopsViewModel @Inject constructor(
    private val getMaterialShopsUseCase: GetMaterialShopsUseCase,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _state = MutableStateFlow(MaterialShopsState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<MaterialShopsEffect>()
    val effect = _effect.asSharedFlow()

    init {
        loadShops()
    }

    private fun loadShops() {
        viewModelScope.launch {
            val villageId = preferenceManager.villageId.firstOrNull()
            if (villageId != null) {
                getMaterialShopsUseCase(villageId).onEach { result ->
                    when (result) {
                        is Resource.Success<*> -> {
                            _state.update {
                                it.copy(
                                    shops = result.data as? List<com.dv.apna.feature.construction.domain.model.MaterialShopModel> ?: emptyList(),
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

    fun onEvent(event: MaterialShopsEvent) {
        when (event) {
            MaterialShopsEvent.BackClick -> {
                viewModelScope.launch { _effect.emit(MaterialShopsEffect.NavigateBack) }
            }
            is MaterialShopsEvent.CallClick -> {
                viewModelScope.launch { _effect.emit(MaterialShopsEffect.DialPhone(event.phone)) }
            }
        }
    }
}
