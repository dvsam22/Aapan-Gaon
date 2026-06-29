package com.dv.apna.feature.construction.presentation.event

sealed interface MaterialShopsEvent {
    data object BackClick : MaterialShopsEvent
    data class CallClick(val phone: String) : MaterialShopsEvent
}
