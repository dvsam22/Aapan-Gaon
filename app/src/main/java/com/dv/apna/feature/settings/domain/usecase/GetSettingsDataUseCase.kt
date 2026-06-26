package com.dv.apna.feature.settings.domain.usecase

import com.dv.apna.feature.settings.domain.repository.SettingsRepository
import javax.inject.Inject

class GetSettingsDataUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    operator fun invoke() = repository.getData()
}