package com.dv.apna.feature.dignitaries.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.dv.apna.core.common.Resource
import com.dv.apna.core.common.UiText
import com.dv.apna.core.datastore.PreferenceManager
import com.dv.apna.core.navigation.Route
import com.dv.apna.feature.dignitaries.domain.usecase.GetDignitariesUseCase
import com.dv.apna.feature.dignitaries.presentation.effect.DignitariesEffect
import com.dv.apna.feature.dignitaries.presentation.event.DignitariesEvent
import com.dv.apna.feature.dignitaries.presentation.state.DignitariesState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DignitariesViewModel @Inject constructor(
    private val getDignitariesUseCase: GetDignitariesUseCase,
    private val preferenceManager: PreferenceManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(DignitariesState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<DignitariesEffect>()
    val effect = _effect.asSharedFlow()

    private var currentVillageId: String = ""
    private var activeCategoryId: String = ""
    private var activeCategoryName: String = ""

    init {
        try {
            val route = savedStateHandle.toRoute<Route.DignitariesDetails>()
            activeCategoryId = route.categoryId
            activeCategoryName = route.categoryName
            _state.update { it.copy(selectedCategory = activeCategoryId, categoryTitle = activeCategoryName) }
        } catch (_: Exception) {}

        observeVillageAndLoad()
    }

    private fun observeVillageAndLoad() {
        viewModelScope.launch {
            preferenceManager.villageId.filterNotNull().collectLatest { villageId ->
                currentVillageId = villageId
                if (activeCategoryId.isNotBlank()) {
                    loadItems(activeCategoryId)
                }
            }
        }
    }

    fun onEvent(event: DignitariesEvent) {
        when (event) {
            is DignitariesEvent.LoadCategory -> {
                activeCategoryId = event.categoryId
                activeCategoryName = event.categoryName
                _state.update { it.copy(selectedCategory = activeCategoryId, categoryTitle = activeCategoryName) }
                loadItems(activeCategoryId)
            }
            DignitariesEvent.Refresh -> {
                if (activeCategoryId.isNotBlank()) {
                    loadItems(activeCategoryId)
                }
            }
            DignitariesEvent.BackClicked -> {
                viewModelScope.launch { _effect.emit(DignitariesEffect.NavigateBack) }
            }
            is DignitariesEvent.CallClicked -> {
                viewModelScope.launch { _effect.emit(DignitariesEffect.DialPhone(event.contact)) }
            }
            is DignitariesEvent.WhatsAppClicked -> {
                viewModelScope.launch { _effect.emit(DignitariesEffect.OpenWhatsApp(event.phone)) }
            }
        }
    }

    private fun loadItems(categoryId: String) {
        viewModelScope.launch {
            getDignitariesUseCase(currentVillageId, categoryId).collectLatest { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                items = result.data ?: emptyList(),
                                error = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = UiText.DynamicString(result.message ?: "Failed to load")
                            )
                        }
                    }
                }
            }
        }
    }
}
