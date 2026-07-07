package com.dv.apna.feature.language.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.dv.apna.core.common.Resource
import com.dv.apna.core.datastore.PreferenceManager
import com.dv.apna.core.navigation.Route
import com.dv.apna.feature.language.domain.usecase.GetLanguageDataUseCase
import com.dv.apna.feature.language.domain.usecase.GetVillagesUseCase
import com.dv.apna.feature.language.domain.usecase.SaveVillageSelectionUseCase
import com.dv.apna.feature.language.presentation.effect.LanguageEffect
import com.dv.apna.feature.language.presentation.event.LanguageEvent
import com.dv.apna.feature.language.presentation.state.LanguageState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val getLanguageDataUseCase: GetLanguageDataUseCase,
    private val getVillagesUseCase: GetVillagesUseCase,
    private val saveVillageSelectionUseCase: SaveVillageSelectionUseCase,
    private val preferenceManager: PreferenceManager,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(LanguageState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<LanguageEffect>()
    val effect = _effect.asSharedFlow()

    init {
        fetchLanguages()
        fetchVillages()
    }

    private fun fetchLanguages() {
        getLanguageDataUseCase().onEach { result ->
            when (result) {
                is Resource.Success<*> -> {
                    _state.update { it.copy(languages = result.data as? List<com.dv.apna.feature.language.domain.model.LanguageModel> ?: emptyList(), isLoading = false) }
                }
                is Resource.Error<*> -> {
                    _state.update { it.copy(error = result.message, isLoading = false) }
                }
                is Resource.Loading<*> -> {
                    _state.update { it.copy(isLoading = true) }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun fetchVillages() {
        combine(
            getVillagesUseCase(),
            preferenceManager.villageId
        ) { result, savedVillageId ->
            when (result) {
                is Resource.Success<*> -> {
                    val villages = result.data as? List<com.dv.apna.feature.language.domain.model.VillageModel> ?: emptyList()
                    
                    // Only auto-select if we are on ChangeVillage or ChangeLanguage screen
                    // If we are on initial Language screen (onboarding), don't pre-select
                    val shouldAutoSelect = try {
                        savedStateHandle.toRoute<Route.ChangeVillage>()
                        true
                    } catch (e: Exception) {
                        try {
                            savedStateHandle.toRoute<Route.ChangeLanguage>()
                            true
                        } catch (e2: Exception) {
                            false
                        }
                    }

                    val selectedVillage = if (shouldAutoSelect) villages.find { it.id == savedVillageId } else null

                    _state.update { 
                        it.copy(
                            villages = villages, 
                            selectedVillage = selectedVillage,
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
    }

    fun onEvent(event: LanguageEvent) {
        when (event) {
            is LanguageEvent.SelectLanguage -> {
                _state.update { it.copy(selectedLanguageId = event.languageId) }
            }
            is LanguageEvent.SelectVillage -> {
                _state.update { it.copy(selectedVillage = event.village) }
            }
            LanguageEvent.Continue -> {
                viewModelScope.launch {
                    val selectedVillage = _state.value.selectedVillage
                    if (selectedVillage != null) {
                        saveVillageSelectionUseCase(selectedVillage.id, selectedVillage.villageName)
                        _effect.emit(LanguageEffect.NavigateToHome)
                    } else {
                        _state.update { it.copy(error = "Please select a village") }
                    }
                }
            }
        }
    }
}
