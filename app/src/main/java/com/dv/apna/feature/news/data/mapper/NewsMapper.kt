package com.dv.apna.feature.news.data.mapper

import com.dv.apna.core.utils.toLocalizedSafeString
import com.dv.apna.feature.news.data.model.NewsDto
import com.dv.apna.feature.news.domain.model.NewsModel
import java.util.Calendar

/**
 * Compensates for GNews free-tier 24-hour publishing delay.
 * If the article is from yesterday or older, it shifts it forward to today
 * (keeping the exact time-of-day), ensuring that yesterday's news seamlessly
 * appears under 'आज' (Today) with today's date, never in the future.
 */
fun adjustNewsDate(rawDate: Long): Long {
    if (rawDate <= 0L) return System.currentTimeMillis()

    val now = Calendar.getInstance()
    val itemCal = Calendar.getInstance().apply { timeInMillis = rawDate }

    val isSameYear = now.get(Calendar.YEAR) == itemCal.get(Calendar.YEAR)
    val isToday = isSameYear && now.get(Calendar.DAY_OF_YEAR) == itemCal.get(Calendar.DAY_OF_YEAR)

    // If already today, keep as is
    if (isToday) {
        return rawDate
    }

    // Shift forward by 24 hours to neutralize the 24h GNews delay
    val shiftedMs = rawDate + (24L * 60 * 60 * 1000L)
    val nowMs = System.currentTimeMillis()

    return if (shiftedMs > nowMs) {
        // If 24h forward slightly lands in the future, pull back to recent past today
        val overshot = shiftedMs - nowMs
        shiftedMs - overshot - (10 * 60 * 1000L) - (rawDate % (5 * 60 * 1000L))
    } else {
        shiftedMs
    }
}

fun cleanNewsText(text: String): String {
    if (text.isBlank()) return text
    return text
        .replace(Regex("(?i)maharajganj\\s*news\\s*[:\\-\\|]\\s*"), "")
        .replace(Regex("(?i)maharajganj\\s*[:\\-\\|]\\s*"), "")
        .replace(Regex("महराजगंज\\s*(न्यूज़|समाचार)?\\s*[:\\-\\|]\\s*"), "")
        .replace(Regex("(?i)read\\s+(latest\\s+)?maharajganj\\s+news\\s+(today\\s+)?in\\s+hindi\\s*[-—–]?\\s*"), "")
        .replace(Regex("(?i)read\\s+latest\\s+news\\s+in\\s+hindi\\s*[-—–]?\\s*"), "")
        .replace(Regex("^(महराजगंज|महाराजगंज)\\s*[।|:\\-—–]\\s*"), "")
        .trim()
}

fun NewsDto.toDomain(languageCode: String): NewsModel {
    val rawDate = date ?: 0L
    val isNotice = category == "notice" || type == "notice"
    val finalDate = if (isNotice) rawDate else adjustNewsDate(rawDate)

    val localizedTitle = title.toLocalizedSafeString(languageCode)
    val localizedDesc = description.toLocalizedSafeString(languageCode)

    return NewsModel(
        id = id ?: "",
        title = if (isNotice) localizedTitle else cleanNewsText(localizedTitle),
        description = if (isNotice) localizedDesc else cleanNewsText(localizedDesc),
        date = finalDate,
        image = image ?: "",
        category = type ?: category ?: "",
        villageId = villageId ?: "",
        source = source ?: "",
        url = url ?: ""
    )
}

/**
 * Maps a list of [NewsDto] to a list of [NewsModel].
 */
fun List<NewsDto>.toDomain(languageCode: String): List<NewsModel> {
    return this.map { it.toDomain(languageCode) }
}
