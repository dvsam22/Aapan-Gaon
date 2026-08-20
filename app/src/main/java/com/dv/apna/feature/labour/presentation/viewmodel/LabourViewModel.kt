package com.dv.apna.feature.labour.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.dv.apna.R
import com.dv.apna.core.common.Resource
import com.dv.apna.core.common.UiText
import com.dv.apna.core.datastore.PreferenceManager
import com.dv.apna.core.navigation.Route
import com.dv.apna.feature.labour.domain.model.LabourService
import com.dv.apna.feature.labour.domain.usecase.GetLaboursByCategoryUseCase
import com.dv.apna.feature.labour.presentation.effect.LabourEffect
import com.dv.apna.feature.labour.presentation.event.LabourEvent
import com.dv.apna.feature.labour.presentation.state.LabourState
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
class LabourViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getLaboursByCategoryUseCase: GetLaboursByCategoryUseCase,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _state = MutableStateFlow(LabourState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<LabourEffect>()
    val effect = _effect.asSharedFlow()

    companion object {
        val ALL_LABOUR_SERVICES = listOf(
            LabourService(UiText.StringResource(R.string.rajmistri), R.drawable.rajmistri, "rajmistri"),
            LabourService(UiText.StringResource(R.string.plumber), R.drawable.plumber, "plumber"),
            LabourService(UiText.StringResource(R.string.electrician), R.drawable.electrician, "electrician"),
            LabourService(UiText.StringResource(R.string.carpenter), R.drawable.carpenter, "carpenter"),
            LabourService(UiText.StringResource(R.string.tailor), R.drawable.tailor, "tailor"),
            LabourService(UiText.StringResource(R.string.painter), R.drawable.painter, "painter"),
            LabourService(UiText.StringResource(R.string.labour), R.drawable.labour, "labour")
        )
    }

    private var loadJob: Job? = null

    init {
        getLabourServices()
        checkLabourDetails()
    }

    private fun checkLabourDetails() {
        try {
            val route = savedStateHandle.toRoute<Route.LabourDetails>()
            val service = ALL_LABOUR_SERVICES.find { it.categoryId.equals(route.category, ignoreCase = true) }
            _state.update { it.copy(
                selectedCategory = route.category,
                selectedCategoryTitle = service?.title ?: UiText.DynamicString(route.category)
            ) }
            fetchLabourDetails(route.category)
        } catch (e: Exception) {
            // Not in LabourDetails route or no args
        }
    }

    private fun getLabourServices() {
        _state.update { it.copy(services = ALL_LABOUR_SERVICES) }
    }

    fun onEvent(event: LabourEvent) {
        when (event) {
            is LabourEvent.BackClick -> {
                viewModelScope.launch { _effect.emit(LabourEffect.NavigateBack) }
            }

            is LabourEvent.CategoryClick -> {
                viewModelScope.launch { _effect.emit(LabourEffect.NavigateToCategory(event.category)) }
            }

            is LabourEvent.Refresh -> {
                _state.value.selectedCategory.let { if (it.isNotEmpty()) fetchLabourDetails(it) }
            }
        }
    }

    private fun fetchLabourDetails(categoryId: String) {
        loadJob?.cancel()
        loadJob = combine(
            preferenceManager.villageId.filterNotNull(),
            preferenceManager.languageCode
        ) { villageId, _ ->
            villageId
        }.flatMapLatest { villageId ->
            getLaboursByCategoryUseCase(villageId, categoryId)
        }
            .onEach { result ->
                when (result) {
                    is Resource.Success<*> -> {
                        _state.update {
                            it.copy(
                                labourDetails = result.data as? List<com.dv.apna.feature.labour.domain.model.LabourDetails>
                                    ?: emptyList(),
                                isLoading = false
                            )
                        }
                    }

                    is Resource.Error<*> -> {
                        _state.update { it.copy(error = UiText.DynamicString(result.message ?: "Unknown error"), isLoading = false) }
                    }

                    is Resource.Loading<*> -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                }
            }.launchIn(viewModelScope)
    }
}
