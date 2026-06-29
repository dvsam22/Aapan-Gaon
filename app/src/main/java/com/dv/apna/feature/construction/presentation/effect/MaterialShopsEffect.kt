package com.dv.apna.feature.construction.presentation.effect

sealed interface MaterialShopsEffect {
    data object NavigateBack : MaterialShopsEffect
    data class DialPhone(val phone: String) : MaterialShopsEffect
}
