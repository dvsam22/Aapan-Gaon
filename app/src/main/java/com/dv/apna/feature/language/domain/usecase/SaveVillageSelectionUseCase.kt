package com.dv.apna.feature.language.domain.usecase

import com.dv.apna.feature.language.domain.repository.LanguageRepository
import javax.inject.Inject

class SaveVillageSelectionUseCase @Inject constructor(
    private val repository: LanguageRepository
) {
    suspend operator fun invoke(id: String, name: String) = repository.saveVillage(id, name)
}
