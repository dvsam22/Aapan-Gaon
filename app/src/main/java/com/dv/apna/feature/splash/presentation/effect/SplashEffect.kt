package com.dv.apna.feature.splash.presentation.effect

sealed interface SplashEffect {
    data object NavigateToNext : SplashEffect
}
