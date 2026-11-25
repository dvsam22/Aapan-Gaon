package com.dv.apna.feature.kya_banega.presentation.util

import android.content.Context
import android.content.Intent
import com.dv.apna.feature.kya_banega.domain.model.RecipeModel
import com.dv.apna.feature.kya_banega.domain.model.stripStepPrefix

object KyaBanegaShareHelper {

    fun formatRecipeShareText(
        recipe: RecipeModel,
        packageName: String,
        isHindi: Boolean = false
    ): String {
        val appLink = "https://play.google.com/store/apps/details?id=$packageName"

        val ingredientsLabel = if (isHindi) "*आवश्यक सामग्री:*" else "*Ingredients Required:*"
        val ingredientsText = if (recipe.ingredientsRequired.isNotEmpty()) {
            "\n$ingredientsLabel\n" + recipe.ingredientsRequired.joinToString("\n") { "• $it" }
        } else ""

        val pantryLabel = if (isHindi) "*मसाले व तेल:*" else "*Common Spices & Oil:*"
        val pantryText = if (recipe.commonPantryItems.isNotEmpty()) {
            "\n\n$pantryLabel\n" + recipe.commonPantryItems.joinToString(", ")
        } else ""

        val stepsLabel = if (isHindi) "*बनाने की सरल विधि:*" else "*Cooking Steps:*"
        val stepsText = if (recipe.steps.isNotEmpty()) {
            "\n\n$stepsLabel\n" + recipe.steps.mapIndexed { index, step ->
                "${index + 1}. ${step.stripStepPrefix()}"
            }.joinToString("\n\n")
        } else ""

        val tipLabel = if (isHindi) "*खास शेफ टिप:*" else "*Chef Secret Tip:*"
        val tipText = if (recipe.chefTip.isNotBlank()) {
            "\n\n$tipLabel\n${recipe.chefTip}"
        } else ""

        val metaLine = if (isHindi) {
            "*समय:* ${recipe.cookingTime} | *मात्रा:* ${recipe.servings} | *स्तर:* ${recipe.difficulty}"
        } else {
            "*Time:* ${recipe.cookingTime} | *Servings:* ${recipe.servings} | *Difficulty:* ${recipe.difficulty}"
        }

        val footerText = if (isHindi) {
            "*रसोई में उपलब्ध सामान से स्वादिष्ट रेसिपी बनाने के लिए 'अपना गाँव' ऐप डाउनलोड करें:*"
        } else {
            "*Download 'Apna Gaon' app to get delicious recipes from ingredients in your kitchen:*"
        }

        return """
*${recipe.name}*
${recipe.subtitle}

$metaLine
$ingredientsText$pantryText$stepsText$tipText

$footerText
$appLink
        """.trimIndent()
    }

    fun shareText(context: Context, text: String, isHindi: Boolean = false) {
        try {
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            }
            val chooserTitle = if (isHindi) "रेसिपी शेयर करें" else "Share Recipe"
            val shareIntent = Intent.createChooser(sendIntent, chooserTitle).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(shareIntent)
        } catch (e: Exception) {
            android.util.Log.e("KyaBanegaShare", "Failed to share text: ${e.message}")
        }
    }
}
