package com.dv.apna.feature.construction.presentation.effect

sealed interface ConstructionDetailEffect {
    data object NavigateBack : ConstructionDetailEffect
    data class DialPhone(val phone: String) : ConstructionDetailEffect
}
