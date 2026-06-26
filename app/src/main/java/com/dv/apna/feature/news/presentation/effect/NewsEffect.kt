package com.dv.apna.feature.news.presentation.effect

sealed interface NewsEffect {
    data class ShowSnackbar(val message: String) : NewsEffect
}