package com.dv.apna.feature.legal_panchayat.presentation.state

import com.dv.apna.core.common.UiText
import com.dv.apna.feature.legal_panchayat.domain.model.LegalPanchayatDetails

data class LegalPanchayatState(
    val isLoading: Boolean = true,
    val items: List<LegalPanchayatDetails> = emptyList(),
    val error: UiText? = null,
    val selectedCategory: String = "",
    val selectedCategoryTitle: UiText? = null
)
