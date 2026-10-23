package com.dv.apna.feature.business.presentation.effect

sealed interface AddBusinessEffect {
    data class ShowToast(val message: String) : AddBusinessEffect
    data object NavigateBack : AddBusinessEffect
}
