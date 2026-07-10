package com.dv.apna.feature.news.data.model

import androidx.annotation.Keep

@Keep
data class NewsDto(
    val category: String? = null,
    val date: Long? = null,
    val description: Any? = null,
    val id: String? = null,
    val image: String? = null,
    val title: Any? = null,
    val villageId: String? = null
)
