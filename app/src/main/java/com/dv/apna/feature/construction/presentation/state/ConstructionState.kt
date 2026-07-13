package com.dv.apna.feature.construction.presentation.state

import com.dv.apna.core.common.UiText

data class ConstructionState(
    val isLoading: Boolean = false,
    val error: UiText? = null
)
