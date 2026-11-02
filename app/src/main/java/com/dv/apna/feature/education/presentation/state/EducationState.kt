package com.dv.apna.feature.education.presentation.state

import com.dv.apna.core.common.UiText
import com.dv.apna.feature.education.domain.model.EducationItemModel

data class EducationState(
    val items: List<EducationItemModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val selectedCategory: String = "",
    val categoryTitle: String = ""
)
