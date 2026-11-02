package com.dv.apna.feature.dignitaries.presentation.state

import com.dv.apna.core.common.UiText
import com.dv.apna.feature.dignitaries.domain.model.DignitaryItemModel

data class DignitariesState(
    val items: List<DignitaryItemModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val selectedCategory: String = "",
    val categoryTitle: String = ""
)
