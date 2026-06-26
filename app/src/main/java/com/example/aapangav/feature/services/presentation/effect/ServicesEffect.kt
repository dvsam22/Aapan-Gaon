package com.example.aapangav.feature.services.presentation.effect

sealed interface ServicesEffect {
    data class ShowSnackbar(val message: String) : ServicesEffect
}