package com.dv.apna.feature.kya_banega.presentation.util

import android.content.Context
import android.content.Intent
import com.dv.apna.feature.kya_banega.domain.engine.CateringCalculatorEngine
import com.dv.apna.feature.kya_banega.domain.model.CateringCalculationResult
import com.dv.apna.feature.kya_banega.domain.model.IngredientCategory
import kotlin.math.roundToInt

object RationShareHelper {

    fun formatRationShareText(
        result: CateringCalculationResult,
        packageName: String,
        isHindi: Boolean = true,
        checkedItems: Set<String> = emptySet()
    ): String {
        val appLink = "https://play.google.com/store/apps/details?id=$packageName"
        val config = result.guestConfig
        val totalPeople = config.adultsCount + config.childrenCount

        val menuNames = result.selectedDishes.joinToString(", ") {
            if (isHindi) it.nameHindi else it.englishName
        }

        val categoryIcon = { cat: IngredientCategory ->
            when (cat) {
                IngredientCategory.KIRANA -> "🛒"
                IngredientCategory.SABZI_MANDI -> "🥦"
                IngredientCategory.DAIRY -> "🥛"
                IngredientCategory.MASALA_DRY -> "🌶️"
            }
        }

        val toBuySectionsList = result.groceryGroups.mapNotNull { group ->
            val remaining = group.items.filter { !checkedItems.contains(it.name) }
            if (remaining.isEmpty()) null else group.category to remaining
        }

        val atHomeItems = result.groceryGroups
            .flatMap { it.items }
            .filter { checkedItems.contains(it.name) }

        val toBuyBody = if (toBuySectionsList.isNotEmpty()) {
            toBuySectionsList.joinToString("\n\n") { (cat, items) ->
                val icon = categoryIcon(cat)
                val itemsList = items.joinToString("\n") {
                    val qty = CateringCalculatorEngine.formatRawAmount(it.totalAmount, it.unit, isHindi)
                    "[ ] ${it.displayName(isHindi)} - *$qty*"
                }
                "$icon *${cat.title(isHindi)}*\n$itemsList"
            }
        } else {
            if (isHindi) "✅ सभी ज़रूरी सामान पहले से घर पर उपलब्ध हैं!" else "✅ All required items are already available at home!"
        }

        val toBuyBlock = if (atHomeItems.isNotEmpty()) {
            val buyHeader = if (isHindi) "🛍️ *बाज़ार से लेने वाला सामान:*" else "🛍️ *Items to Buy (Market):*"
            "$buyHeader\n\n$toBuyBody"
        } else {
            toBuyBody
        }

        val atHomeBlock = if (atHomeItems.isNotEmpty()) {
            val homeHeader = if (isHindi) "🏠 *घर पर उपलब्ध सामान (नहीं लेना है):*" else "🏠 *Already at Home (No Need to Buy):*"
            val homeLines = atHomeItems.joinToString("\n") {
                val qty = CateringCalculatorEngine.formatRawAmount(it.totalAmount, it.unit, isHindi)
                "✓ ${it.displayName(isHindi)} - *$qty*"
            }
            "\n\n━━━━━━━━━━━━━━━━━━━━\n$homeHeader\n$homeLines"
        } else {
            ""
        }

        val budgetSection = if (atHomeItems.isNotEmpty()) {
            val remainingBase = toBuySectionsList
                .flatMap { it.second }
                .sumOf { it.estimatedCost.toDouble() }
                .toFloat()
            val remMin = (remainingBase * 0.95f).roundToInt().coerceAtLeast(0)
            val remMax = (remainingBase * 1.15f).roundToInt().coerceAtLeast(0)
            if (isHindi) {
                "💰 *बाज़ार से लेने का अनुमानित खर्च:* ₹$remMin से ₹$remMax\n" +
                        "🧾 *कुल मेनू बजट:* ₹${result.estimatedCostMin} - ₹${result.estimatedCostMax}"
            } else {
                "💰 *Estimated Market Cost (To Buy):* ₹$remMin to ₹$remMax\n" +
                        "🧾 *Full Menu Budget:* ₹${result.estimatedCostMin} - ₹${result.estimatedCostMax}"
            }
        } else if (isHindi) {
            "💰 *अनुमानित राशन खर्च:* ₹${result.estimatedCostMin} से ₹${result.estimatedCostMax}\n" +
                    "🏷️ *प्रति थाली खर्च:* लगभग ₹${result.costPerPlateMin} - ₹${result.costPerPlateMax}"
        } else {
            "💰 *Estimated Grocery Cost:* ₹${result.estimatedCostMin} to ₹${result.estimatedCostMax}\n" +
                    "🏷️ *Approx. Cost Per Plate:* ₹${result.costPerPlateMin} - ₹${result.costPerPlateMax}"
        }

        val title = if (isHindi) "📋 *राशन व सब्ज़ी पर्ची (सामान लिस्ट)*" else "📋 *Grocery & Ration Shopping List*"
        val occasionLabel = if (isHindi) "🎯 *अवसर:*" else "🎯 *Occasion:*"
        val occasionVal = config.occasion.title(isHindi)
        val guestsLabel = if (isHindi) "👥 *कुल मेहमान:*" else "👥 *Total Guests:*"
        val guestsVal = if (isHindi) {
            "$totalPeople लोग (${config.adultsCount} बड़े + ${config.childrenCount} बच्चे)"
        } else {
            "$totalPeople guests (${config.adultsCount} adults + ${config.childrenCount} children)"
        }
        val menuLabel = if (isHindi) "🍽️ *मेनू:*" else "🍽️ *Menu:*"
        val footer = if (isHindi) {
            "📱 *किसी भी दावत या घर के खाने का सही राशन निकालने के लिए 'अपना गाँव' ऐप डाउनलोड करें:*\n$appLink"
        } else {
            "📱 *Download 'Apna Gaon' app to calculate exact food & grocery quantities for any party:*\n$appLink"
        }

        return """
$title
━━━━━━━━━━━━━━━━━━━━
$occasionLabel $occasionVal
$guestsLabel $guestsVal
$menuLabel $menuNames
━━━━━━━━━━━━━━━━━━━━

$toBuyBlock$atHomeBlock

━━━━━━━━━━━━━━━━━━━━
$budgetSection
━━━━━━━━━━━━━━━━━━━━

$footer
        """.trimIndent()
    }

    fun shareText(context: Context, text: String, isHindi: Boolean = false) {
        try {
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            }
            val chooserTitle = if (isHindi) "राशन पर्ची शेयर करें" else "Share Ration List"
            val shareIntent = Intent.createChooser(sendIntent, chooserTitle).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(shareIntent)
        } catch (e: Exception) {
            android.util.Log.e("RationShare", "Failed to share text: ${e.message}")
        }
    }
}
