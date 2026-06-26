package com.dv.apna.feature.construction.presentation.state

data class ConstructionState(
    val isLoading: Boolean = false,
    val error: String? = null
)