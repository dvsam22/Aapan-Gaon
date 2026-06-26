package com.dv.apna.feature.home.presentation.effect

sealed interface HomeEffect {
    data class ShowSnackbar(val message: String) : HomeEffect
}