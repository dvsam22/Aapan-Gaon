package com.example.aapangav.feature.news.presentation.state

data class NewsState(
    val isLoading: Boolean = false,
    val error: String? = null
)