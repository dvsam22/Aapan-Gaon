package com.dv.apna.feature.splash.presentation.effect

sealed interface SplashEffect {
    data object NavigateToHome : SplashEffect
    data object NavigateToLanguage : SplashEffect
}
