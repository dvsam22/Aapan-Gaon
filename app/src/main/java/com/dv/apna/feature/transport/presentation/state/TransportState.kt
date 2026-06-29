package com.dv.apna.feature.transport.presentation.state

import com.dv.apna.feature.transport.domain.model.TransportDetails
import com.dv.apna.feature.transport.domain.model.TransportService

data class TransportState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val services: List<TransportService> = emptyList(),
    val selectedCategory: String = "",
    val transportDetails: List<TransportDetails> = emptyList()
)
