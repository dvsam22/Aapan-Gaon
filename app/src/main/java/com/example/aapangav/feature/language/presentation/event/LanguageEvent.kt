package com.example.aapangav.feature.language.presentation.event

sealed interface LanguageEvent {
    data class SelectLanguage(val languageId: String) : LanguageEvent
    data object Continue : LanguageEvent
}
