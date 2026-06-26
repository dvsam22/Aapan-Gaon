package com.example.aapangav.feature.settings.presentation.effect

sealed interface SettingsEffect {
    data class ShowSnackbar(val message: String) : SettingsEffect
}