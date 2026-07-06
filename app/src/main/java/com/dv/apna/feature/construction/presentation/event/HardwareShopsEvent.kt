package com.dv.apna.feature.construction.presentation.event

sealed interface HardwareShopsEvent {
    data object BackClick : HardwareShopsEvent
    data class CallClick(val phone: String) : HardwareShopsEvent
}
