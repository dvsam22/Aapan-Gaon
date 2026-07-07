package com.dv.apna.feature.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dv.apna.core.common.Resource
import com.dv.apna.core.datastore.PreferenceManager
import com.dv.apna.feature.home.domain.model.BannerModel
import com.dv.apna.feature.home.domain.usecase.GetHomeDataUseCase
import com.dv.apna.feature.home.presentation.effect.HomeEffect
import com.dv.apna.feature.home.presentation.event.HomeEvent
import com.dv.apna.feature.home.presentation.state.HomeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeDataUseCase: GetHomeDataUseCase,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<HomeEffect>()
    val effect = _effect.asSharedFlow()

    init {
        getHomeData()
        observeVillage()
    }

    private fun observeVillage() {
        preferenceManager.villageName.onEach { village ->
            _state.update { it.copy(selectedVillage = village) }
        }.launchIn(viewModelScope)
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.Refresh -> getHomeData()
        }
    }

    private fun getHomeData() {
        // For now, since we are working on the design, let's immediately show the content with dummy banners
        _state.update { 
            it.copy(
                isLoading = false,
                banners = listOf(
                    BannerModel(
                        id = "1",
                        title = "",
                        discountText = "",
                        backgroundColor = ""
                    ),
                    BannerModel(
                        id = "2",
                        title = "",
                        discountText = "",
                        backgroundColor = ""
                    ),
                    BannerModel(
                        id = "3",
                        title = "",
                        discountText = "",
                        backgroundColor = ""
                    )
                )
            ) 
        }
        
        /* Original logic commented out until backend is ready
        getHomeDataUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.update { it.copy(isLoading = false, data = result.data ?: emptyList()) }
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }
            }
        }.launchIn(viewModelScope)
        */
    }
}
