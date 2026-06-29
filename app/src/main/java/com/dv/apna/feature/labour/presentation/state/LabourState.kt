package com.dv.apna.feature.labour.presentation.state

import com.dv.apna.feature.labour.domain.model.LabourDetails
import com.dv.apna.feature.labour.domain.model.LabourService

data class LabourState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val services: List<LabourService> = emptyList(),
    val selectedCategory: String = "",
    val labourDetails: List<LabourDetails> = emptyList()
)
