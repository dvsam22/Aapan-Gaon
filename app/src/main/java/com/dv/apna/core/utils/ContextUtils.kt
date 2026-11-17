package com.dv.apna.core.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast

fun Context.dial(phone: String) {
    if (phone.isBlank()) return
    val cleanedPhone = phone.trim()
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:$cleanedPhone")
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    try {
        startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
        try {
            Toast.makeText(
                this,
                "Unable to make a call on this device",
                Toast.LENGTH_SHORT
            ).show()
        } catch (_: Exception) {}
    }
}

fun Context.shareApp() {
    try {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Download Aapan Gav App")
            putExtra(
                Intent.EXTRA_TEXT,
                "Download the Aapan Gav App to get the latest local updates and services: https://play.google.com/store/apps/details?id=$packageName"
            )
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val chooser = Intent.createChooser(shareIntent, "Share via").apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(chooser)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Context.rateApp() {
    val playStoreUrl = "https://play.google.com/store/apps/details?id=$packageName"
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(playStoreUrl)
        setPackage("com.android.vending")
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    try {
        startActivity(intent)
    } catch (e: Exception) {
        try {
            val webIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(playStoreUrl)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(webIntent)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
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
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }

    try {
        startActivity(mapIntent)
    } catch (e: Exception) {
        val browserUri = if (lat != 0.0 && lng != 0.0) {
            Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$lat,$lng")
        } else {
            Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${Uri.encode(location)}")
        }
        val browserIntent = Intent(Intent.ACTION_VIEW, browserUri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
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
