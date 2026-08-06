package com.dv.apna.core.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build

fun Context.dial(phone: String) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:$phone")
    }
    startActivity(intent)
}

fun Context.shareApp() {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Download Aapan Gav App")
        putExtra(
            Intent.EXTRA_TEXT,
            "Download the Aapan Gav App to get the latest local updates and services: https://play.google.com/store/apps/details?id=$packageName"
        )
    }
    startActivity(Intent.createChooser(shareIntent, "Share via"))
}

fun Context.rateApp() {
    val playStoreUrl = "https://play.google.com/store/apps/details?id=$packageName"
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(playStoreUrl)
        setPackage("com.android.vending")
    }
    try {
        startActivity(intent)
    } catch (e: Exception) {
        val webIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(playStoreUrl)
        }
        startActivity(webIntent)
    }
}

fun Context.openMap(location: String, lat: Double = 0.0, lng: Double = 0.0) {
    val navigationUri = if (lat != 0.0 && lng != 0.0) {
        Uri.parse("google.navigation:q=$lat,$lng")
    } else if (location.isNotBlank()) {
        Uri.parse("google.navigation:q=${Uri.encode(location)}")
    } else {
        return
    }

    val mapIntent = Intent(Intent.ACTION_VIEW, navigationUri).apply {
        setPackage("com.google.android.apps.maps")
    }

    try {
        startActivity(mapIntent)
    } catch (e: Exception) {
        val browserUri = if (lat != 0.0 && lng != 0.0) {
            Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$lat,$lng")
        } else {
            Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${Uri.encode(location)}")
        }
        val browserIntent = Intent(Intent.ACTION_VIEW, browserUri)
        try {
            startActivity(browserIntent)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}

fun Context.getHtmlAssetUrl(baseName: String): String {
    val language = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        resources.configuration.locales[0].language
    } else {
        @Suppress("DEPRECATION")
        resources.configuration.locale.language
    }
    val isHindi = language.equals("hi", ignoreCase = true)
    val suffix = if (isHindi) "_hi.html" else ".html"
    return "file:///android_asset/${baseName}${suffix}"
}
