package com.dv.apna.feature.language.domain.usecase

import com.dv.apna.feature.language.domain.repository.LanguageRepository
import javax.inject.Inject

class SaveVillageSelectionUseCase @Inject constructor(
    private val repository: LanguageRepository
) {
    suspend operator fun invoke(id: String, name: String, lat: Double = 0.0, lng: Double = 0.0) =
        repository.saveVillage(id, name, lat, lng)
}
