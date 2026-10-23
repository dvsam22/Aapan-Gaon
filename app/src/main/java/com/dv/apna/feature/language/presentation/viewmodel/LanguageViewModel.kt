package com.dv.apna.feature.language.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.dv.apna.core.common.Resource
import com.dv.apna.core.common.UiText
import com.dv.apna.core.datastore.PreferenceManager
import com.dv.apna.core.navigation.Route
import com.dv.apna.feature.language.domain.usecase.GetLanguageDataUseCase
import com.dv.apna.feature.language.domain.usecase.GetVillagesUseCase
import com.dv.apna.feature.language.domain.usecase.SaveLanguageUseCase
import com.dv.apna.feature.language.domain.usecase.SaveVillageSelectionUseCase
import com.dv.apna.feature.language.presentation.effect.LanguageEffect
import com.dv.apna.feature.language.presentation.event.LanguageEvent
import com.dv.apna.feature.language.presentation.state.LanguageState
import com.dv.apna.R
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.dv.apna.feature.language.domain.model.LanguageModel
import com.dv.apna.feature.language.domain.model.VillageModel

@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val getLanguageDataUseCase: GetLanguageDataUseCase,
    private val getVillagesUseCase: GetVillagesUseCase,
    private val saveVillageSelectionUseCase: SaveVillageSelectionUseCase,
    private val saveLanguageUseCase: SaveLanguageUseCase,
    private val preferenceManager: PreferenceManager,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(LanguageState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<LanguageEffect>()
    val effect = _effect.asSharedFlow()

    init {
        fetchData()
    }

    private fun fetchData() {
        combine(
            getLanguageDataUseCase(),
            getVillagesUseCase(),
            preferenceManager.languageCode,
            preferenceManager.villageId
        ) { langResult, villageResult, savedLanguageCode, savedVillageId ->
            val isLoading = langResult is Resource.Loading<*> || villageResult is Resource.Loading<*>
            val error = if (langResult is Resource.Error<*>) {
                UiText.DynamicString(langResult.message ?: "Unknown Error")
            } else if (villageResult is Resource.Error<*>) {
                UiText.DynamicString(villageResult.message ?: "Unknown Error")
            } else null

            val languages = (langResult as? Resource.Success<*>)?.data as? List<LanguageModel> ?: _state.value.languages
            val villages = (villageResult as? Resource.Success<*>)?.data as? List<VillageModel> ?: _state.value.villages

            val selectedLanguageId = if (_state.value.selectedLanguageId == null) {
                languages.find { it.code == savedLanguageCode }?.id
            } else _state.value.selectedLanguageId

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

            val selectedVillage = if (_state.value.selectedVillage == null && (shouldAutoSelect || savedVillageId != null)) {
                villages.find { it.id == savedVillageId }
            } else _state.value.selectedVillage

            _state.update {
                it.copy(
                    languages = languages,
                    villages = villages,
                    selectedLanguageId = selectedLanguageId,
                    selectedVillage = selectedVillage,
                    isLoading = isLoading,
                    error = error
                )
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
                    val currentState = _state.value
                    val selectedLanguage = currentState.languages.find { it.id == currentState.selectedLanguageId }
                    
                    // Use village from state, or fallback to saved village if we are just changing language
                    val villageId = currentState.selectedVillage?.id ?: preferenceManager.villageId.firstOrNull()
                    val villageName = currentState.selectedVillage?.villageName ?: preferenceManager.villageName.firstOrNull()
                    val villageLat = currentState.selectedVillage?.lat ?: preferenceManager.villageLat.firstOrNull() ?: 0.0
                    val villageLng = currentState.selectedVillage?.lng ?: preferenceManager.villageLng.firstOrNull() ?: 0.0

                    if (villageId != null && selectedLanguage != null) {
                        _state.update { it.copy(isLoading = true) }

                        // 1. Unsubscribe from old topic if needed
                        try {
                            val currentSavedVillageId = preferenceManager.villageId.firstOrNull()
                            if (currentSavedVillageId != null && currentSavedVillageId != villageId) {
                                val oldTopic = "village_$currentSavedVillageId"
                                FirebaseMessaging.getInstance().unsubscribeFromTopic(oldTopic)
                            }
                        } catch (e: Exception) {
                        }

                        // 2. Save everything and WAIT (Atomic)
                        saveVillageSelectionUseCase(villageId, villageName ?: "", villageLat, villageLng)
                        saveLanguageUseCase(selectedLanguage.code)
                        
                        // 3. Subscribe to new topic
                        try {
                            val newTopic = "village_$villageId"
                            FirebaseMessaging.getInstance().subscribeToTopic(newTopic)
                        } catch (e: Exception) {
                        }

                        // 4. Trigger navigation and let CompositionLocalProvider update the language dynamically (no recreation).
                        _effect.emit(LanguageEffect.NavigateToHome)

                    } else if (selectedLanguage == null) {
                        _state.update { it.copy(error = UiText.StringResource(R.string.error_select_language)) }
                    } else {
                        _state.update { it.copy(error = UiText.StringResource(R.string.error_select_village)) }
                    }
                }
            }
        }
    }
}
