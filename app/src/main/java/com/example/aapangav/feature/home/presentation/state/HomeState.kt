package com.example.aapangav.feature.home.presentation.state

import com.example.aapangav.feature.home.domain.model.HomeModel

data class HomeState(
    val isLoading: Boolean = false,
    val data: List<HomeModel> = emptyList(),
    val error: String? = null
)