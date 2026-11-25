package com.dv.apna.feature.kya_banega.presentation.effect

sealed interface KyaBanegaEffect {
    data class ShowToast(val message: String) : KyaBanegaEffect
    data class LaunchVoiceInput(val prompt: String? = null) : KyaBanegaEffect
}
