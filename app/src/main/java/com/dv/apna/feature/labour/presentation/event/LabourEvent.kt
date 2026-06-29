package com.dv.apna.feature.labour.presentation.event

sealed interface LabourEvent {
    data object BackClick : LabourEvent
    data class CategoryClick(val category: String) : LabourEvent
}
