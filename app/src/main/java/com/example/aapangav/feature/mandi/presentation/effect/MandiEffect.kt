package com.example.aapangav.feature.mandi.presentation.effect

sealed interface MandiEffect {
    data class ShowSnackbar(val message: String) : MandiEffect
}