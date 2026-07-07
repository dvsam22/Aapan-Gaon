package com.dv.apna.feature.health.domain.usecase

import com.dv.apna.feature.health.domain.repository.HealthRepository
import javax.inject.Inject

class GetDoctorsUseCase @Inject constructor(
    private val repository: HealthRepository
) {
    operator fun invoke(villageId: String) = repository.getDoctors(villageId)
}
