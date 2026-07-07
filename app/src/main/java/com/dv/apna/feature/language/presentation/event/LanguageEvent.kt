package com.dv.apna.feature.language.presentation.event

import com.dv.apna.feature.language.domain.model.VillageModel

sealed interface LanguageEvent {
    data class SelectLanguage(val languageId: String) : LanguageEvent
    data class SelectVillage(val village: VillageModel) : LanguageEvent
    data object Continue : LanguageEvent
}
