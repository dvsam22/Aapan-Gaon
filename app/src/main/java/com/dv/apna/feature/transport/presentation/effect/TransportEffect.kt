package com.dv.apna.feature.transport.presentation.effect

sealed interface TransportEffect {
    data object NavigateBack : TransportEffect
    data class NavigateToCategory(val categoryId: String, val categoryName: String) : TransportEffect
    data class DialPhone(val contact: String) : TransportEffect
}
