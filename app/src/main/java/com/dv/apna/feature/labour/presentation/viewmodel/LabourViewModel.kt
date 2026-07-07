package com.dv.apna.feature.labour.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.dv.apna.R
import com.dv.apna.core.common.Resource
import com.dv.apna.core.datastore.PreferenceManager
import com.dv.apna.core.navigation.Route
import com.dv.apna.feature.labour.domain.model.LabourService
import com.dv.apna.feature.labour.domain.usecase.GetLaboursByCategoryUseCase
import com.dv.apna.feature.labour.presentation.effect.LabourEffect
import com.dv.apna.feature.labour.presentation.event.LabourEvent
import com.dv.apna.feature.labour.presentation.state.LabourState
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
class LabourViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getLaboursByCategoryUseCase: GetLaboursByCategoryUseCase,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _state = MutableStateFlow(LabourState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<LabourEffect>()
    val effect = _effect.asSharedFlow()

    init {
        getLabourServices()
        checkLabourDetails()
    }

    private fun checkLabourDetails() {
        try {
            val route = savedStateHandle.toRoute<Route.LabourDetails>()
            _state.update { it.copy(selectedCategory = route.category) }
            fetchLabourDetails(route.category)
        } catch (e: Exception) {
            // Not in LabourDetails route or no args
        }
    }

    private fun getLabourServices() {
        val services = listOf(
            LabourService("Rajmistri", R.drawable.rajmistri, "rajmistri"),
            LabourService("Plumber", R.drawable.plumber, "plumber"),
            LabourService("Electrician", R.drawable.electrician, "electrician"),
            LabourService("Carpenter", R.drawable.carpenter, "carpenter"),
            LabourService("Tailor", R.drawable.tailor, "tailor"),
            LabourService("Labour", R.drawable.labour, "labour")
        )
        _state.update { it.copy(services = services) }
    }

    fun onEvent(event: LabourEvent) {
        when (event) {
            is LabourEvent.BackClick -> {
                viewModelScope.launch { _effect.emit(LabourEffect.NavigateBack) }
            }

            is LabourEvent.CategoryClick -> {
                val categoryId = _state.value.services.find { it.title == event.category }?.categoryId ?: event.category
                viewModelScope.launch { _effect.emit(LabourEffect.NavigateToCategory(categoryId)) }
            }
        }
    }

    private fun fetchLabourDetails(categoryId: String) {
        viewModelScope.launch {
            val villageId = preferenceManager.villageId.firstOrNull()
            if (villageId != null) {
                getLaboursByCategoryUseCase(villageId, categoryId).onEach { result ->
                    when (result) {
                        is Resource.Success<*> -> {
                            _state.update { 
                                it.copy(
                                    labourDetails = result.data as? List<com.dv.apna.feature.labour.domain.model.LabourDetails> ?: emptyList(),
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
}
