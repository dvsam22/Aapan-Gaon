package com.dv.apna.feature.legal_panchayat.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.dv.apna.R
import com.dv.apna.core.common.Resource
import com.dv.apna.core.common.UiText
import com.dv.apna.core.datastore.PreferenceManager
import com.dv.apna.core.navigation.Route
import com.dv.apna.feature.legal_panchayat.domain.usecase.GetLegalPanchayatByCategoryUseCase
import com.dv.apna.feature.legal_panchayat.presentation.effect.LegalPanchayatEffect
import com.dv.apna.feature.legal_panchayat.presentation.event.LegalPanchayatEvent
import com.dv.apna.feature.legal_panchayat.presentation.state.LegalPanchayatState
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
class LegalPanchayatViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getLegalPanchayatByCategoryUseCase: GetLegalPanchayatByCategoryUseCase,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _state = MutableStateFlow(LegalPanchayatState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<LegalPanchayatEffect>()
    val effect = _effect.asSharedFlow()

    private var loadJob: Job? = null

    init {
        checkDetails()
    }

    private fun checkDetails() {
        try {
            val route = savedStateHandle.toRoute<Route.LegalPanchayatDetails>()
            val categoryTitle = when (route.categoryId) {
                "court_lawyers" -> UiText.StringResource(R.string.legal_court_lawyers)
                "panchayat_officials" -> UiText.StringResource(R.string.legal_panchayat_officials)
                "senior_citizens" -> UiText.StringResource(R.string.legal_senior_citizens)
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

    fun onEvent(event: LegalPanchayatEvent) {
        when (event) {
            is LegalPanchayatEvent.BackClick -> {
                viewModelScope.launch { _effect.emit(LegalPanchayatEffect.NavigateBack) }
            }
            is LegalPanchayatEvent.Refresh -> {
                _state.value.selectedCategory.let { if (it.isNotEmpty()) fetchDetails(it) }
            }
            is LegalPanchayatEvent.CallClick -> {
                viewModelScope.launch { _effect.emit(LegalPanchayatEffect.DialPhone(event.contact)) }
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
            getLegalPanchayatByCategoryUseCase(villageId, categoryId)
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
