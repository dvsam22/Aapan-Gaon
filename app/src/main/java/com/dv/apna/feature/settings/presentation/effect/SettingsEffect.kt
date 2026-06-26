package com.dv.apna.feature.settings.presentation.effect

sealed interface SettingsEffect {
    data class ShowSnackbar(val message: String) : SettingsEffect
}