package com.dv.apna.feature.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dv.apna.core.common.Resource
import com.dv.apna.core.datastore.PreferenceManager
import com.dv.apna.feature.home.domain.usecase.GetBannersUseCase
import com.dv.apna.feature.home.presentation.effect.HomeEffect
import com.dv.apna.feature.home.presentation.event.HomeEvent
import com.dv.apna.feature.home.presentation.state.HomeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getBannersUseCase: GetBannersUseCase,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<HomeEffect>()
    val effect = _effect.asSharedFlow()

    init {
        observeVillage()
        fetchBanners()
    }

    private fun observeVillage() {
        preferenceManager.villageName.onEach { village ->
            _state.update { it.copy(selectedVillage = village) }
        }.launchIn(viewModelScope)
    }

    private fun fetchBanners() {
        viewModelScope.launch {
            preferenceManager.villageId.flatMapLatest { villageId ->
                if (villageId != null) {
                    getBannersUseCase(villageId)
                } else {
                    kotlinx.coroutines.flow.flowOf(Resource.Success(emptyList()))
                }
            }.collectLatest { result ->
                when (result) {
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                banners = result.data ?: emptyList()
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false, error = result.message) }
                    }
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.Refresh -> fetchBanners()
        }
    }
}
