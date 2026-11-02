package com.dv.apna.feature.dignitaries.presentation.event

sealed interface DignitariesEvent {
    data class LoadCategory(val categoryId: String, val categoryName: String) : DignitariesEvent
    data object Refresh : DignitariesEvent
    data object BackClicked : DignitariesEvent
    data class CallClicked(val contact: String) : DignitariesEvent
    data class WhatsAppClicked(val phone: String) : DignitariesEvent
}
