package com.dv.apna.feature.language.domain.usecase

import com.dv.apna.feature.language.domain.repository.LanguageRepository
import javax.inject.Inject

class GetVillagesUseCase @Inject constructor(
    private val repository: LanguageRepository
) {
    operator fun invoke() = repository.getVillages()
}
