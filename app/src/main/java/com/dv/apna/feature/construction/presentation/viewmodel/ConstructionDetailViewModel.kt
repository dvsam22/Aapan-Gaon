package com.dv.apna.feature.construction.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dv.apna.core.common.Resource
import com.dv.apna.core.common.UiText
import com.dv.apna.core.datastore.PreferenceManager
import com.dv.apna.feature.construction.domain.usecase.GetConstructionItemsUseCase
import com.dv.apna.feature.construction.presentation.effect.ConstructionDetailEffect
import com.dv.apna.feature.construction.presentation.event.ConstructionDetailEvent
import com.dv.apna.feature.construction.presentation.state.ConstructionDetailState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConstructionDetailViewModel @Inject constructor(
    private val getConstructionItemsUseCase: GetConstructionItemsUseCase,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _state = MutableStateFlow(ConstructionDetailState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ConstructionDetailEffect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: ConstructionDetailEvent) {
        when (event) {
            is ConstructionDetailEvent.LoadItems -> {
                loadItems(event.categoryId)
            }
            is ConstructionDetailEvent.BackClick -> {
                viewModelScope.launch {
                    _effect.emit(ConstructionDetailEffect.NavigateBack)
                }
            }
            is ConstructionDetailEvent.CallClick -> {
                viewModelScope.launch {
                    _effect.emit(ConstructionDetailEffect.DialPhone(event.phone))
                }
            }
            is ConstructionDetailEvent.Refresh -> {
                _state.value.categoryId?.let { loadItems(it) }
            }
        }
    }

    private fun loadItems(categoryId: String) {
        viewModelScope.launch {
            val villageId = preferenceManager.villageId.firstOrNull()
            if (villageId == null) {
                _state.update { it.copy(error = UiText.StringResource(com.dv.apna.R.string.error_village_not_selected)) }
                return@launch
            }

            getConstructionItemsUseCase(villageId, categoryId).collectLatest { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null, categoryId = categoryId) }
                    }
                    is Resource.Success -> {
                        _state.update { it.copy(isLoading = false, items = result.data ?: emptyList(), error = null, categoryId = categoryId) }
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false, error = UiText.DynamicString(result.message ?: "Unknown error"), categoryId = categoryId) }
                    }
                }
            }
        }
    }
}
