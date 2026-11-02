package com.dv.apna.feature.education.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.dv.apna.core.common.Resource
import com.dv.apna.core.common.UiText
import com.dv.apna.core.datastore.PreferenceManager
import com.dv.apna.core.navigation.Route
import com.dv.apna.feature.education.domain.usecase.GetEducationItemsUseCase
import com.dv.apna.feature.education.presentation.effect.EducationEffect
import com.dv.apna.feature.education.presentation.event.EducationEvent
import com.dv.apna.feature.education.presentation.state.EducationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EducationViewModel @Inject constructor(
    private val getEducationItemsUseCase: GetEducationItemsUseCase,
    private val preferenceManager: PreferenceManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(EducationState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<EducationEffect>()
    val effect = _effect.asSharedFlow()

    private var currentVillageId: String = ""
    private var activeCategoryId: String = ""
    private var activeCategoryName: String = ""

    init {
        try {
            val route = savedStateHandle.toRoute<Route.EducationDetails>()
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

    fun onEvent(event: EducationEvent) {
        when (event) {
            is EducationEvent.LoadCategory -> {
                activeCategoryId = event.categoryId
                activeCategoryName = event.categoryName
                _state.update { it.copy(selectedCategory = activeCategoryId, categoryTitle = activeCategoryName) }
                loadItems(activeCategoryId)
            }
            EducationEvent.Refresh -> {
                if (activeCategoryId.isNotBlank()) {
                    loadItems(activeCategoryId)
                }
            }
            EducationEvent.BackClicked -> {
                viewModelScope.launch { _effect.emit(EducationEffect.NavigateBack) }
            }
            is EducationEvent.CallClicked -> {
                viewModelScope.launch { _effect.emit(EducationEffect.DialPhone(event.contact)) }
            }
            is EducationEvent.WhatsAppClicked -> {
                viewModelScope.launch { _effect.emit(EducationEffect.OpenWhatsApp(event.phone)) }
            }
            is EducationEvent.OpenLink -> {
                viewModelScope.launch { _effect.emit(EducationEffect.OpenBrowser(event.url)) }
            }
        }
    }

    private fun loadItems(categoryId: String) {
        viewModelScope.launch {
            getEducationItemsUseCase(currentVillageId, categoryId).collectLatest { result ->
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
