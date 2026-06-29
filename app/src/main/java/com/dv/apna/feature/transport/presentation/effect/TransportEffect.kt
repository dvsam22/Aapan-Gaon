package com.dv.apna.feature.transport.presentation.effect

sealed interface TransportEffect {
    data object NavigateBack : TransportEffect
    data class NavigateToCategory(val category: String) : TransportEffect
}
