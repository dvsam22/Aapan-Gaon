package com.dv.apna.feature.notification.data.mapper

import com.dv.apna.core.utils.toLocalizedSafeString
import com.dv.apna.feature.notification.data.model.NotificationDto
import com.dv.apna.feature.notification.domain.model.NotificationModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

fun NotificationDto.toDomain(languageCode: String): NotificationModel {
    val dateObj = Date(this.date)
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val dateFormat = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
    
    val timeStr = timeFormat.format(dateObj)
    val dateStr = dateFormat.format(dateObj)
    
    val now = Calendar.getInstance()
    val notificationCalendar = Calendar.getInstance().apply { time = dateObj }
    
    val category = when {
        isSameDay(now, notificationCalendar) -> "Today"
        isYesterday(now, notificationCalendar) -> "Yesterday"
        else -> dateStr
    }
    
    val timeAgo = getTimeAgo(this.date)
    
    val localizedTitle = this.title.toLocalizedSafeString(languageCode)
    val localizedMessage = this.message.toLocalizedSafeString(languageCode)

    return NotificationModel(
        id = this.id,
        title = localizedTitle,
        summary = if (localizedMessage.length > 50) localizedMessage.take(50) + "..." else localizedMessage,
        description = localizedMessage,
        time = timeAgo,
        date = "$category, $timeStr",
        category = category
    )
}

private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

private fun isYesterday(now: Calendar, then: Calendar): Boolean {
    val yesterday = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, -1)
    }
    return isSameDay(yesterday, then)
}

private fun getTimeAgo(time: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - time
    
    return when {
        diff < TimeUnit.MINUTES.toMillis(1) -> "Just now"
        diff < TimeUnit.HOURS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toMinutes(diff)} min ago"
        diff < TimeUnit.DAYS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toHours(diff)} hr ago"
        diff < TimeUnit.DAYS.toMillis(2) -> "Yesterday"
        else -> "${TimeUnit.MILLISECONDS.toDays(diff)} days ago"
    }
}
