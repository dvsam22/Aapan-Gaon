package com.dv.apna.feature.dignitaries.presentation.effect

sealed interface DignitariesEffect {
    data object NavigateBack : DignitariesEffect
    data class DialPhone(val contact: String) : DignitariesEffect
    data class OpenWhatsApp(val phone: String) : DignitariesEffect
}
