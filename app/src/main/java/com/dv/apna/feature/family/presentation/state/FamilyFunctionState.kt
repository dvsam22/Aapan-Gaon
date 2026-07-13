package com.dv.apna.feature.family.presentation.state

import com.dv.apna.core.common.UiText
import com.dv.apna.feature.family.domain.model.FamilyFunctionDetails

data class FamilyFunctionState(
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val selectedCategory: String = "",
    val selectedCategoryTitle: UiText? = null,
    val familyFunctionDetails: List<FamilyFunctionDetails> = emptyList()
)
