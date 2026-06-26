package com.example.aapangav.feature.splash.domain.usecase

import com.example.aapangav.feature.splash.domain.repository.SplashRepository
import javax.inject.Inject

class GetSplashDataUseCase @Inject constructor(
    private val repository: SplashRepository
) {
    operator fun invoke() = repository.getData()
}