package com.dv.apna.feature.kya_banega.domain.model

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class RecipeModel(
    val id: String = "",
    val name: String = "",
    val subtitle: String = "",
    val cookingTime: String = "",
    val difficulty: String = "",
    val servings: String = "",
    val category: String = "",
    val ingredientsRequired: List<String> = emptyList(),
    val commonPantryItems: List<String> = emptyList(),
    val steps: List<String> = emptyList(),
    val chefTip: String = ""
)

@Keep
@Serializable
data class RecipeSuggestionResponse(
    val recipes: List<RecipeModel> = emptyList(),
    val generalAdvice: String = ""
)

private val STEP_PREFIX_REGEX = Regex(
    "^\\s*(?:(?:Step|चरण)\\s*\\d+\\s*[:.\\-–)]*\\s*|\\d+(?:[):\\-–]+|\\.(?!\\d))\\s*)",
    RegexOption.IGNORE_CASE
)

/**
 * Strips leading numbering/prefix like "Step 1:", "चरण 1:", "1.", "1)" while preserving quantities like "1.5 cups".
 */
fun String.stripStepPrefix(): String = replaceFirst(STEP_PREFIX_REGEX, "").trim()
