package com.dv.apna.feature.construction.presentation.state

import com.dv.apna.core.common.UiText
import com.dv.apna.feature.construction.domain.model.ConstructionItemModel

data class ConstructionDetailState(
    val isLoading: Boolean = false,
    val items: List<ConstructionItemModel> = emptyList(),
    val error: UiText? = null,
    val categoryId: String? = null
)
