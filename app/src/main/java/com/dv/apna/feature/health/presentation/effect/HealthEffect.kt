package com.dv.apna.feature.health.presentation.effect

sealed interface HealthEffect {
    data class ShowSnackbar(val message: String) : HealthEffect
}