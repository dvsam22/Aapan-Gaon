package com.dv.apna.core.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

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
