package com.dv.apna.feature.language.presentation.effect

sealed interface LanguageEffect {
    data object NavigateToHome : LanguageEffect
    data object NavigateBack : LanguageEffect
}
