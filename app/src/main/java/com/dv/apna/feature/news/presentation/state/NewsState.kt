package com.dv.apna.feature.news.presentation.state

import com.dv.apna.core.common.UiText
import com.dv.apna.feature.news.domain.model.NewsModel

data class NewsState(
    val isLoading: Boolean = true,
    val news: List<NewsModel> = emptyList(),
    val notices: List<NewsModel> = emptyList(),
    val error: UiText? = null
)
