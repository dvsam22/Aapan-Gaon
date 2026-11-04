package com.dv.apna.feature.education.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.dv.apna.R
import com.dv.apna.core.common.Resource
import com.dv.apna.core.common.UiText
import com.dv.apna.core.datastore.PreferenceManager
import com.dv.apna.core.navigation.Route
import com.dv.apna.feature.education.domain.usecase.GetEducationByCategoryUseCase
import com.dv.apna.feature.education.presentation.effect.EducationEffect
import com.dv.apna.feature.education.presentation.event.EducationEvent
import com.dv.apna.feature.education.presentation.state.EducationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class EducationViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getEducationByCategoryUseCase: GetEducationByCategoryUseCase,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _state = MutableStateFlow(EducationState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<EducationEffect>()
    val effect = _effect.asSharedFlow()

    private var loadJob: Job? = null

    init {
        checkDetails()
    }

    private fun checkDetails() {
        try {
            val route = savedStateHandle.toRoute<Route.EducationDetails>()
            val categoryTitle = when (route.categoryId) {
                "private_schools" -> UiText.StringResource(R.string.education_private_schools)
                "coaching_centers" -> UiText.StringResource(R.string.education_coaching_centers)
                "home_tutors" -> UiText.StringResource(R.string.education_home_tutors)
                "skill_centers" -> UiText.StringResource(R.string.education_skill_centers)
                else -> UiText.DynamicString(route.title.ifBlank { route.categoryId })
            }
            _state.update {
                it.copy(
                    selectedCategory = route.categoryId,
                    selectedCategoryTitle = categoryTitle
                )
            }
            fetchDetails(route.categoryId)
        } catch (e: Exception) {
            // Not in Details route
        }
    }

    fun onEvent(event: EducationEvent) {
        when (event) {
            is EducationEvent.BackClick -> {
                viewModelScope.launch { _effect.emit(EducationEffect.NavigateBack) }
            }
            is EducationEvent.Refresh -> {
                _state.value.selectedCategory.let { if (it.isNotEmpty()) fetchDetails(it) }
            }
            is EducationEvent.CallClick -> {
                viewModelScope.launch { _effect.emit(EducationEffect.DialPhone(event.contact)) }
            }
        }
    }

    private fun fetchDetails(categoryId: String) {
        loadJob?.cancel()
        loadJob = combine(
            preferenceManager.villageId,
            preferenceManager.languageCode
        ) { villageId, _ ->
            villageId ?: ""
        }.flatMapLatest { villageId ->
            getEducationByCategoryUseCase(villageId, categoryId)
        }.onEach { result ->
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
                            error = UiText.DynamicString(result.message ?: "Failed to load data")
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }
}
