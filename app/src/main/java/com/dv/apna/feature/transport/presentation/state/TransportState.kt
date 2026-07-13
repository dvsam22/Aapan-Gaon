package com.dv.apna.feature.transport.presentation.state

import com.dv.apna.core.common.UiText
import com.dv.apna.feature.transport.domain.model.TransportDetails
import com.dv.apna.feature.transport.domain.model.TransportService

data class TransportState(
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val services: List<TransportService> = emptyList(),
    val selectedCategory: String = "",
    val selectedCategoryTitle: UiText? = null,
    val transportDetails: List<TransportDetails> = emptyList()
)
