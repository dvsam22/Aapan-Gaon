package com.dv.apna.feature.language.domain.usecase

import com.dv.apna.feature.language.domain.repository.LanguageRepository
import javax.inject.Inject

class SaveLanguageUseCase @Inject constructor(
    private val repository: LanguageRepository
) {
    suspend operator fun invoke(code: String) {
        repository.saveLanguage(code)
    }
}
