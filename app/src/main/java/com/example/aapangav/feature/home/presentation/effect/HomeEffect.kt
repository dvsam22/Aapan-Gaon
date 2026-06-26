package com.example.aapangav.feature.home.presentation.effect

sealed interface HomeEffect {
    data class ShowSnackbar(val message: String) : HomeEffect
}