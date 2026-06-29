package com.dv.apna.feature.construction.presentation.state

import com.dv.apna.feature.construction.domain.model.MaterialShopModel

data class MaterialShopsState(
    val isLoading: Boolean = false,
    val shops: List<MaterialShopModel> = emptyList(),
    val error: String? = null
)
