package com.dv.apna.feature.construction.presentation.effect

sealed interface BricksEffect {
    data object NavigateBack : BricksEffect
    data class DialPhone(val phone: String) : BricksEffect
}
