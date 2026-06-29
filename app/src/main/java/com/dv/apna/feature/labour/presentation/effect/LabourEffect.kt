package com.dv.apna.feature.labour.presentation.effect

sealed interface LabourEffect {
    data object NavigateBack : LabourEffect
    data class NavigateToCategory(val category: String) : LabourEffect
}
