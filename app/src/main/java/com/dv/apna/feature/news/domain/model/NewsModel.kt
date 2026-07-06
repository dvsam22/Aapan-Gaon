package com.dv.apna.feature.news.domain.model

data class NewsModel(
    val id: String = "",
    val title: String = "",
    val summary: String = "",
    val description: String = "",
    val time: String = "",
    val imageUrl: String = ""
)

data class NoticeModel(
    val id: String = "",
    val title: String = "",
    val summary: String = "",
    val description: String = "",
    val date: String = ""
)
