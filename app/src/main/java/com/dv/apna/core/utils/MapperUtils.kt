package com.dv.apna.core.utils

fun Map<String, String>?.toLocalizedString(languageCode: String): String {
    return this?.get(languageCode) ?: this?.get("en") ?: ""
}

fun Any?.toLocalizedSafeString(languageCode: String): String {
    return when (this) {
        is String -> this
        is Map<*, *> -> {
            val map = this as? Map<String, String>
            map?.get(languageCode) ?: map?.get("en") ?: map?.values?.firstOrNull() ?: ""
        }
        else -> ""
    }
}
