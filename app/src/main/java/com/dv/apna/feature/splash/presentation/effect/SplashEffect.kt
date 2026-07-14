package com.dv.apna.feature.splash.presentation.effect

sealed interface SplashEffect {
    data object NavigateToHome : SplashEffect
    data object NavigateToLanguage : SplashEffect
    data class NavigateToNotificationDetails(val id: String, val type: String?) : SplashEffect
}
