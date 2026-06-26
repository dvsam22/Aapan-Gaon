package com.dv.apna.feature.home.presentation.event

sealed interface HomeEvent {
    data object Refresh : HomeEvent
}