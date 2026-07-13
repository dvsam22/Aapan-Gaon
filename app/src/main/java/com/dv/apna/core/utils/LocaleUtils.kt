package com.dv.apna.core.utils

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

object LocaleUtils {
    fun setLocale(languageCode: String) {
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(languageCode)
        if (AppCompatDelegate.getApplicationLocales() != appLocale) {
            AppCompatDelegate.setApplicationLocales(appLocale)
        }
    }
}
