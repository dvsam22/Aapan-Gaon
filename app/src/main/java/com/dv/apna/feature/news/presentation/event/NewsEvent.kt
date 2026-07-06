package com.dv.apna.feature.news.presentation.event

sealed interface NewsEvent {
    data object BackClick : NewsEvent
    data class NewsClick(val id: String) : NewsEvent
    data class NoticeClick(val id: String) : NewsEvent
}
