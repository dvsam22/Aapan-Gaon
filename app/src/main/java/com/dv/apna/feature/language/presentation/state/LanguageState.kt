package com.dv.apna.feature.language.presentation.state

import com.dv.apna.feature.language.domain.model.LanguageModel

data class LanguageState(
    val languages: List<LanguageModel> = listOf(
        LanguageModel("1", "English", "English", "en"),
        LanguageModel("2", "Hindi", "हिंदी", "hi")
    ),
    val selectedLanguageId: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
