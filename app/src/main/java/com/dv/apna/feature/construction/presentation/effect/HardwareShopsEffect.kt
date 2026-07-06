package com.dv.apna.feature.construction.presentation.effect

sealed interface HardwareShopsEffect {
    data object NavigateBack : HardwareShopsEffect
    data class DialPhone(val phone: String) : HardwareShopsEffect
}
