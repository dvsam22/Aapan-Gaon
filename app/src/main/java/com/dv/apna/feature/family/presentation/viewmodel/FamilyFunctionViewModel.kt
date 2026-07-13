package com.dv.apna.feature.family.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.dv.apna.R
import com.dv.apna.core.common.Resource
import com.dv.apna.core.common.UiText
import com.dv.apna.core.datastore.PreferenceManager
import com.dv.apna.core.navigation.Route
import com.dv.apna.feature.family.domain.usecase.GetFamilyFunctionsByCategoryUseCase
import com.dv.apna.feature.family.presentation.effect.FamilyFunctionEffect
import com.dv.apna.feature.family.presentation.event.FamilyFunctionEvent
import com.dv.apna.feature.family.presentation.state.FamilyFunctionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class FamilyFunctionViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getFamilyFunctionsByCategoryUseCase: GetFamilyFunctionsByCategoryUseCase,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _state = MutableStateFlow(FamilyFunctionState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<FamilyFunctionEffect>()
    val effect = _effect.asSharedFlow()

    private var loadJob: Job? = null

    init {
        checkDetails()
    }

    private fun checkDetails() {
        try {
            val route = savedStateHandle.toRoute<Route.FamilyFunctionDetails>()
            val categoryTitle = when (route.categoryId) {
                "tent" -> UiText.StringResource(R.string.tent_pandal_decor)
                "catering" -> UiText.StringResource(R.string.catering_halwai)
                "photo" -> UiText.StringResource(R.string.photo_videography)
                "dj" -> UiText.StringResource(R.string.dj_sound_band)
                "marriage_halls" -> UiText.StringResource(R.string.marriage_halls_lawns)
                else -> UiText.DynamicString(route.categoryId)
            }
            _state.update { it.copy(
                selectedCategory = route.categoryId,
                selectedCategoryTitle = categoryTitle
            ) }
            fetchDetails(route.categoryId)
        } catch (e: Exception) {
            // Not in Details route
        }
    }

    fun onEvent(event: FamilyFunctionEvent) {
        when (event) {
            is FamilyFunctionEvent.BackClick -> {
                viewModelScope.launch { _effect.emit(FamilyFunctionEffect.NavigateBack) }
            }

            is FamilyFunctionEvent.CategoryClick -> {
                viewModelScope.launch { _effect.emit(FamilyFunctionEffect.NavigateToDetails(event.category)) }
            }

            is FamilyFunctionEvent.Refresh -> {
                _state.value.selectedCategory.let { if (it.isNotEmpty()) fetchDetails(it) }
            }

            is FamilyFunctionEvent.CallClick -> {
                viewModelScope.launch { _effect.emit(FamilyFunctionEffect.DialPhone(event.contact)) }
            }
        }
    }

    private fun fetchDetails(categoryId: String) {
        loadJob?.cancel()
        loadJob = combine(
            preferenceManager.villageId.filterNotNull(),
            preferenceManager.languageCode
        ) { villageId, _ ->
            villageId
        }.flatMapLatest { villageId ->
            getFamilyFunctionsByCategoryUseCase(villageId, categoryId)
        }
            .onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                familyFunctionDetails = result.data ?: emptyList(),
                                isLoading = false
                            )
                        }
                    }

                    is Resource.Error -> {
                        _state.update { it.copy(error = UiText.DynamicString(result.message ?: "Unknown error"), isLoading = false) }
                    }

                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                }
            }.launchIn(viewModelScope)
    }
}
