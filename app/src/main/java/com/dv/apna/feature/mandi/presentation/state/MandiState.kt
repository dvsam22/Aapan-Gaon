package com.dv.apna.feature.mandi.presentation.state

data class MandiState(
    val isLoading: Boolean = false,
    val error: String? = null
)