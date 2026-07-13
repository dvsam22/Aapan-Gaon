package com.dv.apna.feature.construction.presentation.state

import com.dv.apna.core.common.UiText
import com.dv.apna.feature.construction.domain.model.HardwareShopModel

data class HardwareShopsState(
    val isLoading: Boolean = false,
    val shops: List<HardwareShopModel> = emptyList(),
    val error: UiText? = null
)
