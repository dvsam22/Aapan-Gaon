package com.dv.apna.feature.language.presentation.state

import com.dv.apna.feature.language.domain.model.LanguageModel
import com.dv.apna.feature.language.domain.model.VillageModel

data class LanguageState(
    val languages: List<LanguageModel> = emptyList(),
    val selectedLanguageId: String? = null,
    val villages: List<VillageModel> = emptyList(),
    val selectedVillage: VillageModel? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
