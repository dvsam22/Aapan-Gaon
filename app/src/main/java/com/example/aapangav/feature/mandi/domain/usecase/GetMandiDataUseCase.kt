package com.example.aapangav.feature.mandi.domain.usecase

import com.example.aapangav.feature.mandi.domain.repository.MandiRepository
import javax.inject.Inject

class GetMandiDataUseCase @Inject constructor(
    private val repository: MandiRepository
) {
    operator fun invoke() = repository.getData()
}