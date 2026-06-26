package com.dv.apna.feature.mandi.presentation.effect

sealed interface MandiEffect {
    data class ShowSnackbar(val message: String) : MandiEffect
}