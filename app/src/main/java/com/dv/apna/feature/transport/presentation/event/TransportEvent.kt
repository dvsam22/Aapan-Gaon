package com.dv.apna.feature.transport.presentation.event

sealed interface TransportEvent {
    data object BackClick : TransportEvent
    data class CategoryClick(val categoryId: String, val categoryName: String) : TransportEvent
    data class CallClick(val contact: String) : TransportEvent
}
