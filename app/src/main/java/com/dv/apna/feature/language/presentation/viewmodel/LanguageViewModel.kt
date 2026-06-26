package com.dv.apna.feature.language.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dv.apna.feature.language.presentation.effect.LanguageEffect
import com.dv.apna.feature.language.presentation.event.LanguageEvent
import com.dv.apna.feature.language.presentation.state.LanguageState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(LanguageState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<LanguageEffect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: LanguageEvent) {
        when (event) {
            is LanguageEvent.SelectLanguage -> {
                _state.update { it.copy(selectedLanguageId = event.languageId) }
            }
            LanguageEvent.Continue -> {
                viewModelScope.launch {
                    _effect.emit(LanguageEffect.NavigateToHome)
                }
            }
        }
    }
}
