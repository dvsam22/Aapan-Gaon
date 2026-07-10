package com.dv.apna.feature.construction.presentation.event

sealed interface BricksEvent {
    data object BackClick : BricksEvent
    data class CallClick(val phone: String) : BricksEvent
    data object Refresh : BricksEvent
}
