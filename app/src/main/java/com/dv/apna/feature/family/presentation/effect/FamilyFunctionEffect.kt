package com.dv.apna.feature.family.presentation.effect

sealed interface FamilyFunctionEffect {
    data object NavigateBack : FamilyFunctionEffect
    data class NavigateToDetails(val categoryId: String) : FamilyFunctionEffect
    data class DialPhone(val contact: String) : FamilyFunctionEffect
}
