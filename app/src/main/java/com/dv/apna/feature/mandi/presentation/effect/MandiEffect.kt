package com.dv.apna.feature.mandi.presentation.effect

sealed interface MandiEffect {
    data object NavigateBack : MandiEffect
    data object NavigateToCropPrices : MandiEffect
    data object NavigateToTodayMarket : MandiEffect
    data object NavigateToLocalBuyers : MandiEffect
}
