package com.example.aapangav.feature.health.presentation.effect

sealed interface HealthEffect {
    data class ShowSnackbar(val message: String) : HealthEffect
}