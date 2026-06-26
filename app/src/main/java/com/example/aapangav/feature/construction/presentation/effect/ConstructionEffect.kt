package com.example.aapangav.feature.construction.presentation.effect

sealed interface ConstructionEffect {
    data class ShowSnackbar(val message: String) : ConstructionEffect
}