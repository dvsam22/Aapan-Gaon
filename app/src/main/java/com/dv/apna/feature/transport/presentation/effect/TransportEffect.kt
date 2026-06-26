package com.dv.apna.feature.transport.presentation.effect

sealed interface TransportEffect {
    data class ShowSnackbar(val message: String) : TransportEffect
}