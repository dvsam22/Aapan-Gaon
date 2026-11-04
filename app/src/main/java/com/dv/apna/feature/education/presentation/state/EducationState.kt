package com.dv.apna.feature.education.presentation.state

import com.dv.apna.core.common.UiText
import com.dv.apna.feature.education.domain.model.EducationDetails

data class EducationState(
    val isLoading: Boolean = true,
    val items: List<EducationDetails> = emptyList(),
    val error: UiText? = null,
    val selectedCategory: String = "",
    val selectedCategoryTitle: UiText? = null
)
