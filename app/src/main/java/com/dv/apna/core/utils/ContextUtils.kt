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
