package com.dv.apna.feature.transport.presentation.event

sealed interface TransportEvent {
    data object BackClick : TransportEvent
    data class CategoryClick(val category: String) : TransportEvent
}
