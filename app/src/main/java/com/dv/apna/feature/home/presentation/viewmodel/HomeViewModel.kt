package com.dv.apna.feature.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dv.apna.core.common.Resource
import com.dv.apna.core.common.UiText
import com.dv.apna.core.datastore.PreferenceManager
import com.dv.apna.feature.home.domain.model.BannerModel
import com.dv.apna.feature.home.domain.usecase.GetBannersUseCase
import com.dv.apna.feature.home.presentation.effect.HomeEffect
import com.dv.apna.feature.home.presentation.event.HomeEvent
import com.dv.apna.feature.home.presentation.state.HomeState
import com.dv.apna.feature.language.domain.usecase.GetVillagesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.dv.apna.R
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getBannersUseCase: GetBannersUseCase,
    private val getVillagesUseCase: GetVillagesUseCase,
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
        viewModelScope.launch {
            combine(
                preferenceManager.villageId.filterNotNull(),
                preferenceManager.villageName,
                preferenceManager.villageLat,
                preferenceManager.villageLng,
                getVillagesUseCase()
            ) { villageId, savedName, savedLat, savedLng, villagesResource ->
                val villages = (villagesResource as? Resource.Success<*>)?.data as? List<com.dv.apna.feature.language.domain.model.VillageModel>
                val village = villages?.find { it.id == villageId }
                if (village != null) {
                    Triple(village.villageName, village.lat, village.lng)
                } else {
                    Triple(savedName ?: "", savedLat, savedLng)
                }
            }.collectLatest { (name, lat, lng) ->
                if (name.isNotEmpty()) {
                    _state.update {
                        it.copy(
                            selectedVillage = name,
                            selectedVillageLat = lat,
                            selectedVillageLng = lng
                        )
                    }
                }
            }
        }
    }

    private fun fetchBanners() {
        viewModelScope.launch {
            combine(
                preferenceManager.villageId,
                preferenceManager.languageCode
            ) { villageId, languageCode ->
                villageId to languageCode
            }.flatMapLatest { (villageId, _) ->
                if (villageId != null) {
                    getBannersUseCase(villageId)
                } else {
                    kotlinx.coroutines.flow.flowOf(Resource.Error("Village not selected"))
                }
            }.collectLatest { result ->
                when (result) {
                    is Resource.Success<*> -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                banners = result.data as? List<BannerModel> ?: emptyList()
                            )
                        }
                    }
                    is Resource.Error<*> -> {
                        val errorMessage = if (result.message == "Village not selected") {
                            UiText.StringResource(R.string.error_village_not_selected)
                        } else {
                            UiText.DynamicString(result.message ?: "An error occurred")
                        }
                        _state.update { it.copy(isLoading = false, error = errorMessage) }
                    }
                    is Resource.Loading<*> -> {
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
