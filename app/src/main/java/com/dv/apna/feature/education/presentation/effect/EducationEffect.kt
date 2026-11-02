package com.dv.apna.feature.education.presentation.effect

sealed interface EducationEffect {
    data object NavigateBack : EducationEffect
    data class DialPhone(val contact: String) : EducationEffect
    data class OpenWhatsApp(val phone: String) : EducationEffect
    data class OpenBrowser(val url: String) : EducationEffect
}
