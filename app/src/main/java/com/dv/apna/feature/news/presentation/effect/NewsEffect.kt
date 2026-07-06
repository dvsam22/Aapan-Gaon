package com.dv.apna.feature.news.presentation.effect

sealed interface NewsEffect {
    data object NavigateBack : NewsEffect
    data class NavigateToNewsDetails(val id: String) : NewsEffect
    data class NavigateToNoticeDetails(val id: String) : NewsEffect
}
