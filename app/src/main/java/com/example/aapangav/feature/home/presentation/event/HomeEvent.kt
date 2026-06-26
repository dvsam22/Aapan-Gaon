package com.example.aapangav.feature.home.presentation.event

sealed interface HomeEvent {
    data object Refresh : HomeEvent
}