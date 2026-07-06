package com.dv.apna.feature.news.presentation.state

import com.dv.apna.feature.news.domain.model.NewsModel
import com.dv.apna.feature.news.domain.model.NoticeModel

data class NewsState(
    val isLoading: Boolean = false,
    val breakingNews: List<NewsModel> = emptyList(),
    val notices: List<NoticeModel> = emptyList(),
    val error: String? = null
)
