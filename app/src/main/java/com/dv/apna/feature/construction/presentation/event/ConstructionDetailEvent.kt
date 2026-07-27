package com.dv.apna.feature.construction.presentation.event

sealed interface ConstructionDetailEvent {
    data class LoadItems(val categoryId: String) : ConstructionDetailEvent
    data object BackClick : ConstructionDetailEvent
    data class CallClick(val phone: String) : ConstructionDetailEvent
    data object Refresh : ConstructionDetailEvent
}
