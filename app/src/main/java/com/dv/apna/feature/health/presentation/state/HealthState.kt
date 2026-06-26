package com.dv.apna.feature.health.presentation.state

data class HealthState(
    val isLoading: Boolean = false,
    val error: String? = null
)