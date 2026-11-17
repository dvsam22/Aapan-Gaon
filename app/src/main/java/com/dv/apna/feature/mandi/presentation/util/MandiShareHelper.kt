package com.dv.apna.feature.mandi.presentation.util

import android.content.Context
import android.content.Intent
import com.dv.apna.feature.mandi.domain.model.MarketPriceModel

object MandiShareHelper {

    fun formatMarketPricesShareText(
        items: List<MarketPriceModel>,
        dateText: String,
        packageName: String
    ): String {
        val appLink = "https://play.google.com/store/apps/details?id=$packageName"
        val topItems = items.take(15)

        val itemsText = topItems.joinToString("\n") { item ->
            val trendEmoji = when (item.trend.lowercase()) {
                "up" -> "📈"
                "down" -> "📉"
                else -> "➖"
            }
            val priceStr = if (item.price % 1.0 == 0.0) item.price.toLong().toString() else item.price.toString()
            "• ${item.name}: ₹$priceStr / ${item.unit} $trendEmoji"
        }

        return """
🌾 *आज का ताज़ा मंडी भाव* 🌾
📅 दिनांक: $dateText

$itemsText

📲 *गाँव व मंडी के ताज़ा भाव पाने के लिए 'अपना गाँव' ऐप डाउनलोड करें:*
$appLink
        """.trimIndent()
    }

    fun shareText(context: Context, text: String) {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        try {
            val waIntent = Intent(sendIntent).apply {
                setPackage("com.whatsapp")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(waIntent)
        } catch (_: Exception) {
            try {
                val wbIntent = Intent(sendIntent).apply {
                    setPackage("com.whatsapp.w4b")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(wbIntent)
            } catch (_: Exception) {
                val chooser = Intent.createChooser(sendIntent, "Share via").apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(chooser)
            }
        }
    }
}
