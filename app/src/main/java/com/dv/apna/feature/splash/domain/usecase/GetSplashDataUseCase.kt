package com.dv.apna.feature.splash.domain.usecase

import com.dv.apna.feature.splash.domain.repository.SplashRepository
import javax.inject.Inject

class GetSplashDataUseCase @Inject constructor(
    private val repository: SplashRepository
) {
    operator fun invoke() = repository.getData()
}