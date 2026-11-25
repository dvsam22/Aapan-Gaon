package com.dv.apna.feature.kya_banega.data.repository

import com.dv.apna.core.common.GeminiContentHelper
import com.dv.apna.core.common.Resource
import com.dv.apna.core.datastore.PreferenceManager
import com.dv.apna.feature.kya_banega.domain.model.RecipeModel
import com.dv.apna.feature.kya_banega.domain.model.RecipeSuggestionResponse
import com.dv.apna.feature.kya_banega.domain.model.stripStepPrefix
import com.dv.apna.feature.kya_banega.domain.repository.KyaBanegaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KyaBanegaRepositoryImpl @Inject constructor(
    private val preferenceManager: PreferenceManager
) : KyaBanegaRepository {

    override fun generateRecipes(
        ingredients: List<String>,
        mealType: String?
    ): Flow<Resource<RecipeSuggestionResponse>> = flow {
        emit(Resource.Loading())
        val languageCode = preferenceManager.languageCode.firstOrNull() ?: "en"
        val isHindi = languageCode.equals("hi", ignoreCase = true)
        try {
            val ingredientsListStr = ingredients.joinToString(", ")
            val mealTypePrompt = when (mealType) {
                "quick" -> "Preferred meal type: Quick Breakfast (ready in around 15 minutes)."
                "lunch" -> "Preferred meal type: Wholesome Lunch or Dinner."
                "snacks" -> "Preferred meal type: Tea-Time Snacks."
                else -> if (!mealType.isNullOrBlank()) {
                    "Preferred meal type or occasion: $mealType."
                } else {
                    "Suggest 3 diverse recipes suitable for everyday Indian village and home kitchens."
                }
            }

            val prompt = buildRecipePrompt(
                ingredientsListStr = ingredientsListStr,
                mealTypePrompt = mealTypePrompt,
                isHindi = isHindi
            )

            val fallbackError = if (isHindi) {
                "रेसिपी प्राप्त नहीं हो सकी, कृपया दोबारा प्रयास करें।"
            } else {
                "Could not fetch recipes, please try again."
            }
            val responseJson = GeminiContentHelper.generateJson(
                promptText = prompt,
                temperature = 0.25f,
                maxOutputTokens = 950,
                fallbackErrorMessage = fallbackError,
                logTag = "KyaBanega"
            )
            val parsedResult = parseRecipeResponse(responseJson, isHindi)
            emit(Resource.Success(parsedResult))
        } catch (e: Exception) {
            val fallbackMsg = if (isHindi) {
                "रेसिपी खोजने में त्रुटि हुई, कृपया पुनः प्रयास करें।"
            } else {
                "Something went wrong while finding recipes, please try again."
            }
            emit(Resource.Error(GeminiContentHelper.extractErrorMessage(e, fallbackMsg)))
        }
    }.flowOn(Dispatchers.IO)

    private fun buildRecipePrompt(
        ingredientsListStr: String,
        mealTypePrompt: String,
        isHindi: Boolean
    ): String {
        val toneAndLanguageRule = if (isHindi) {
            """
            Keep the tone warm, respectful, and authentic Indian Hindi.
            CRITICAL LANGUAGE RULE: Respond strictly in simple, natural Hindi (Devanagari script) for ALL JSON field values (generalAdvice, name, subtitle, cookingTime, difficulty, servings, category, ingredientsRequired, commonPantryItems, steps, chefTip).
            """.trimIndent()
        } else {
            """
            Keep the tone warm, clear, and helpful in simple everyday Indian English.
            CRITICAL LANGUAGE RULE: Respond strictly in English for ALL JSON field values (generalAdvice, name, subtitle, cookingTime, difficulty, servings, category, ingredientsRequired, commonPantryItems, steps, chefTip). Write Indian dish and spice names in English/Roman script (e.g., "Crispy Aloo Pyaz Pakora", "Jeera Aloo", "15 mins", "Easy", "3-4 people", "Tea Time Snack", "1 tsp cumin seeds (jeera)"). Do NOT use Hindi Devanagari script anywhere in the JSON.
            """.trimIndent()
        }

        val schemaExample = if (isHindi) {
            """
            {
              "generalAdvice": "एक छोटी और उत्साहवर्धक पंक्ति (हिंदी में)",
              "recipes": [
                {
                  "id": "1",
                  "name": "व्यंजन का नाम हिंदी में (जैसे: कुरकुरी आलू-प्याज टिक्की, स्वादिष्ट बेसन कढ़ी)",
                  "subtitle": "एक पंक्ति का आकर्षक विवरण (जैसे: 15 मिनट में तैयार गरमा-गरम चाय का साथी)",
                  "cookingTime": "लगने वाला समय (जैसे: 15 मिनट, 25 मिनट)",
                  "difficulty": "कठिनाई स्तर (जैसे: बहुत सरल, मध्यम)",
                  "servings": "कितने लोगों के लिए (जैसे: 3-4 लोग)",
                  "category": "श्रेणी (जैसे: झटपट नाश्ता, दोपहर का खाना, चटपटा स्नैक)",
                  "ingredientsRequired": [
                    "सामग्री 1 मात्रा सहित (जैसे: 2 उबले आलू)",
                    "सामग्री 2 मात्रा सहित (जैसे: 1 कप बेसन)"
                  ],
                  "commonPantryItems": [
                    "आम मसाले (जैसे: स्वादानुसार नमक, हल्दी, लाल मिर्च पाउडर, सरसों का तेल)"
                  ],
                  "steps": [
                    "सबसे पहले आलू और प्याज़ को धोकर काट लें...",
                    "अब एक कड़ाही में तेल गरम करके जीरा चटकाएं...",
                    "मध्यम आंच पर सुनहरा होने तक पकाएं..."
                  ],
                  "chefTip": "स्वाद बढ़ाने के लिए एक ख़ास देसी शेफ टिप (जैसे: कुरकुरा बनाने के लिए 1 चम्मच सूजी मिलाएं)"
                }
              ]
            }
            """.trimIndent()
        } else {
            """
            {
              "generalAdvice": "One short encouraging tip in English",
              "recipes": [
                {
                  "id": "1",
                  "name": "Dish name in English (e.g., Crispy Potato-Onion Bhajiya, Jeera Aloo)",
                  "subtitle": "One-line appetizing description in English (e.g., Crispy evening snack ready in 15 mins)",
                  "cookingTime": "Time required in English (e.g., 15 mins, 25 mins)",
                  "difficulty": "Difficulty level in English (e.g., Very Easy, Easy, Medium)",
                  "servings": "Serving size in English (e.g., 3-4 people)",
                  "category": "Category in English (e.g., Quick Breakfast, Lunch & Dinner, Tea Time Snack)",
                  "ingredientsRequired": [
                    "Ingredient 1 with quantity in English (e.g., 2 boiled potatoes)",
                    "Ingredient 2 with quantity in English (e.g., 1 cup gram flour (besan))"
                  ],
                  "commonPantryItems": [
                    "Common spices in English (e.g., Salt to taste, turmeric powder, red chilli powder, cooking oil)"
                  ],
                  "steps": [
                    "First, wash and chop the potatoes and onions...",
                    "Heat oil in a pan and let cumin seeds splutter...",
                    "Cook on medium flame until tender..."
                  ],
                  "chefTip": "A helpful desi chef tip in English (e.g., Add 1 tbsp semolina for extra crispiness)"
                }
              ]
            }
            """.trimIndent()
        }

        return """
            You are an expert Indian Home Chef specialized in delicious, authentic, and budget-friendly Indian home cooking.
            The user has the following ingredients available at home:
            $ingredientsListStr

            $mealTypePrompt

            Your task is to recommend 3 distinct, practical, mouthwatering Indian dishes that can be prepared primarily using these available ingredients, plus common Indian basic kitchen staples (salt, turmeric, red chilli, cumin, oil/ghee, water).
            Keep responses concise for fast reading: 3 to 5 required ingredients, 2 to 4 pantry items, and 3 to 4 short clear action steps per recipe.
            Do NOT include step numbers or prefixes like "Step 1:" or "चरण 1:" inside the strings of the "steps" array.
            $toneAndLanguageRule

            Respond ONLY with a valid JSON object matching this exact schema (no markdown, no extra explanation text):
            $schemaExample
        """.trimIndent()
    }

    private fun parseRecipeResponse(jsonString: String, isHindi: Boolean): RecipeSuggestionResponse {
        val root = JSONObject(jsonString)
        val defaultAdvice = if (isHindi) {
            "आज की स्वादिष्ट रेसिपी आपके लिए तैयार हैं!"
        } else {
            "Delicious recipes are ready for you!"
        }
        val generalAdvice = root.optString("generalAdvice", defaultAdvice)
        val recipesArray = root.optJSONArray("recipes") ?: JSONArray()
        val recipeList = mutableListOf<RecipeModel>()

        val defaultName = if (isHindi) "स्वादिष्ट व्यंजन" else "Delicious Dish"
        val defaultTime = if (isHindi) "20 मिनट" else "20 mins"
        val defaultDifficulty = if (isHindi) "सरल" else "Easy"
        val defaultServings = if (isHindi) "2-3 लोग" else "2-3 people"
        val defaultCategory = if (isHindi) "घरेलू पकवान" else "Home Cooking"

        for (i in 0 until recipesArray.length()) {
            val item = recipesArray.optJSONObject(i) ?: continue

            val ingredientsRequiredList = mutableListOf<String>()
            val ingArray = item.optJSONArray("ingredientsRequired")
            if (ingArray != null) {
                for (j in 0 until ingArray.length()) {
                    ingredientsRequiredList.add(ingArray.optString(j))
                }
            }

            val commonPantryList = mutableListOf<String>()
            val pantryArray = item.optJSONArray("commonPantryItems")
            if (pantryArray != null) {
                for (j in 0 until pantryArray.length()) {
                    commonPantryList.add(pantryArray.optString(j))
                }
            }

            val stepsList = mutableListOf<String>()
            val stepsArray = item.optJSONArray("steps")
            if (stepsArray != null) {
                for (j in 0 until stepsArray.length()) {
                    val cleanStep = stepsArray.optString(j).stripStepPrefix()
                    if (cleanStep.isNotBlank()) {
                        stepsList.add(cleanStep)
                    }
                }
            }

            recipeList.add(
                RecipeModel(
                    id = item.optString("id", (i + 1).toString()),
                    name = item.optString("name", defaultName),
                    subtitle = item.optString("subtitle", ""),
                    cookingTime = item.optString("cookingTime", defaultTime),
                    difficulty = item.optString("difficulty", defaultDifficulty),
                    servings = item.optString("servings", defaultServings),
                    category = item.optString("category", defaultCategory),
                    ingredientsRequired = ingredientsRequiredList,
                    commonPantryItems = commonPantryList,
                    steps = stepsList,
                    chefTip = item.optString("chefTip", "")
                )
            )
        }

        return RecipeSuggestionResponse(
            recipes = recipeList,
            generalAdvice = generalAdvice
        )
    }
}
