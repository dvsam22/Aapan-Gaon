package com.dv.apna.feature.transport.presentation.state

data class TransportState(
    val isLoading: Boolean = false,
    val error: String? = null
)