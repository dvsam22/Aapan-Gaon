package com.dv.apna.feature.family.presentation.event

sealed interface FamilyFunctionEvent {
    data object BackClick : FamilyFunctionEvent
    data class CategoryClick(val category: String) : FamilyFunctionEvent
    data object Refresh : FamilyFunctionEvent
    data class CallClick(val contact: String) : FamilyFunctionEvent
}
