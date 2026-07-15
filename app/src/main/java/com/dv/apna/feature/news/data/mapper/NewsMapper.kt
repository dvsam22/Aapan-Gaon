package com.dv.apna.feature.news.data.mapper

import com.dv.apna.core.utils.toLocalizedSafeString
import com.dv.apna.feature.news.data.model.NewsDto
import com.dv.apna.feature.news.domain.model.NewsModel

fun NewsDto.toDomain(languageCode: String): NewsModel {
    return NewsModel(
        id = id ?: "",
        title = title.toLocalizedSafeString(languageCode),
        description = description.toLocalizedSafeString(languageCode),
        date = date ?: 0L,
        image = image ?: "",
        category = type ?: category ?: "",
        villageId = villageId ?: ""
    )
}

/**
 * Maps a list of [NewsDto] to a list of [NewsModel].
 */
fun List<NewsDto>.toDomain(languageCode: String): List<NewsModel> {
    return this.map { it.toDomain(languageCode) }
}
