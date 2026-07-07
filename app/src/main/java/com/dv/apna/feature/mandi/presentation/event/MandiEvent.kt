package com.dv.apna.feature.mandi.presentation.event

sealed interface MandiEvent {
    data object BackClick : MandiEvent
    data object CropPricesClick : MandiEvent
    data object TodayMarketClick : MandiEvent
    data object LocalBuyersClick : MandiEvent
    data class CallClick(val phone: String) : MandiEvent
}
