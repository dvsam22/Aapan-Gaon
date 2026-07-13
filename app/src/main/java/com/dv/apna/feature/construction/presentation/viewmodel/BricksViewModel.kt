package com.dv.apna.feature.construction.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dv.apna.core.common.Resource
import com.dv.apna.core.common.UiText
import com.dv.apna.core.datastore.PreferenceManager
import com.dv.apna.feature.construction.domain.usecase.GetBricksSuppliersUseCase
import com.dv.apna.feature.construction.presentation.effect.BricksEffect
import com.dv.apna.feature.construction.presentation.event.BricksEvent
import com.dv.apna.feature.construction.presentation.state.BricksState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class BricksViewModel @Inject constructor(
    private val getBricksSuppliersUseCase: GetBricksSuppliersUseCase,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _state = MutableStateFlow(BricksState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<BricksEffect>()
    val effect = _effect.asSharedFlow()

    private var loadJob: Job? = null

    init {
        loadSuppliers()
    }

    private fun loadSuppliers() {
        loadJob?.cancel()
        loadJob = combine(
            preferenceManager.villageId.filterNotNull(),
            preferenceManager.languageCode
        ) { villageId, _ ->
            villageId
        }.flatMapLatest { villageId ->
                getBricksSuppliersUseCase(villageId)
            }
            .onEach { result ->
                when (result) {
                    is Resource.Success<*> -> {
                        _state.update {
                            it.copy(
                                suppliers = result.data as? List<com.dv.apna.feature.construction.domain.model.BricksSupplierModel>
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

    fun onEvent(event: BricksEvent) {
        when (event) {
            is BricksEvent.BackClick -> {
                viewModelScope.launch { _effect.emit(BricksEffect.NavigateBack) }
            }

            is BricksEvent.CallClick -> {
                viewModelScope.launch { _effect.emit(BricksEffect.DialPhone(event.phone)) }
            }

            is BricksEvent.Refresh -> {
                loadSuppliers()
            }
        }
    }
}
