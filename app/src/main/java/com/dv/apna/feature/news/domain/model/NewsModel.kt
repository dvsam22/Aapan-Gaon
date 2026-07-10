package com.dv.apna.feature.news.domain.model

data class NewsModel(
    val id: String,
    val title: String,
    val description: String,
    val date: Long,
    val image: String,
    val category: String,
    val villageId: String
)
