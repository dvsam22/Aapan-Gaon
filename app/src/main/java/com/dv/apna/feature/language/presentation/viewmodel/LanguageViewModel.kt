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
import com.dv.apna.feature.language.domain.usecase.SaveLanguageUseCase
import com.dv.apna.feature.language.domain.usecase.SaveVillageSelectionUseCase
import com.dv.apna.feature.language.presentation.effect.LanguageEffect
import com.dv.apna.feature.language.presentation.event.LanguageEvent
import com.dv.apna.feature.language.presentation.state.LanguageState
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
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
            val error = (langResult as? Resource.Error<*>)?.message ?: (villageResult as? Resource.Error<*>)?.message

            val languages = (langResult as? Resource.Success<*>)?.data as? List<com.dv.apna.feature.language.domain.model.LanguageModel> ?: _state.value.languages
            val villages = (villageResult as? Resource.Success<*>)?.data as? List<com.dv.apna.feature.language.domain.model.VillageModel> ?: _state.value.villages

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

            val selectedVillage = if (_state.value.selectedVillage == null && shouldAutoSelect) {
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
                    val selectedVillage = currentState.selectedVillage
                    val selectedLanguageId = currentState.selectedLanguageId
                    val selectedLanguage = currentState.languages.find { it.id == selectedLanguageId }

                    if (selectedVillage != null && selectedLanguage != null) {
                        // Handle FCM Topic Subscription
                        try {
                            val oldVillageId = preferenceManager.villageId.firstOrNull()
                            if (oldVillageId != null && oldVillageId != selectedVillage.id) {
                                val oldTopic = if (oldVillageId.startsWith("village_")) oldVillageId else "village_$oldVillageId"
                                FirebaseMessaging.getInstance().unsubscribeFromTopic(oldTopic)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Log.d("FCM", "Successfully unsubscribed from: $oldTopic")
                                        }
                                    }
                            }
                            
                            if (oldVillageId != selectedVillage.id) {
                                val topicName = if (selectedVillage.id.startsWith("village_")) selectedVillage.id else "village_${selectedVillage.id}"
                                FirebaseMessaging.getInstance().subscribeToTopic(topicName)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Log.d("FCM", "Successfully subscribed to: $topicName")
                                        }
                                    }
                            }
                        } catch (e: Exception) {
                            Log.e("FCM", "Error in FCM subscription: ${e.message}")
                        }

                        saveVillageSelectionUseCase(selectedVillage.id, selectedVillage.villageName)
                        saveLanguageUseCase(selectedLanguage.code)
                        com.dv.apna.core.utils.LocaleUtils.setLocale(selectedLanguage.code)
                        _effect.emit(LanguageEffect.NavigateToHome)
                    } else if (selectedLanguage == null) {
                        _state.update { it.copy(error = "Please select a language") }
                    } else {
                        _state.update { it.copy(error = "Please select a village") }
                    }
                }
            }
        }
    }
}
