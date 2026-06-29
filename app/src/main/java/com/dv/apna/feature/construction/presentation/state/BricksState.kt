package com.dv.apna.feature.construction.presentation.state

import com.dv.apna.feature.construction.domain.model.BricksSupplierModel

data class BricksState(
    val isLoading: Boolean = false,
    val suppliers: List<BricksSupplierModel> = emptyList(),
    val error: String? = null
)
