package com.dv.apna.feature.kya_banega.domain.model

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
enum class OccasionType(
    val titleHindi: String,
    val titleEnglish: String,
    val subtitleHindi: String,
    val subtitleEnglish: String,
    val defaultGuests: Int
) {
    HOME(
        "घर का खाना",
        "Home Cooking",
        "3 से 8 लोग (दैनिक पारिवारिक भोजन)",
        "3 to 8 persons (daily family cooking)",
        5
    ),
    PARTY(
        "पार्टी व दावत",
        "Party & Celebration",
        "10 से 35 लोग (जन्मदिन, सालगिरह, किटी पार्टी)",
        "10 to 35 persons (Birthday, Anniversary, Get-together)",
        15
    ),
    BHANDARA(
        "भंडारा व बड़ा आयोजन",
        "Bhandara & Catering",
        "50 से 500+ लोग (कथा, भंडारा, विवाह)",
        "50 to 500+ persons (Katha, Wedding, Catering)",
        100
    );

    fun title(isHindi: Boolean): String = if (isHindi) titleHindi else titleEnglish
    fun subtitle(isHindi: Boolean): String = if (isHindi) subtitleHindi else subtitleEnglish
}

@Keep
@Serializable
enum class AppetiteLevel(
    val labelHindi: String,
    val labelEnglish: String,
    val multiplier: Float,
    val noteHindi: String,
    val noteEnglish: String
) {
    LIGHT(
        "कम भूख (0.8x)",
        "Light (0.8x)",
        0.8f,
        "बुज़ुर्ग या हल्का भोजन करने वाले",
        "Elders or light eaters"
    ),
    NORMAL(
        "सामान्य भूख (1.0x)",
        "Normal (1.0x)",
        1.0f,
        "मानक भारतीय भोजन खुराक",
        "Standard portion per guest"
    ),
    HEAVY(
        "शौकीन भूख (1.25x)",
        "Hearty (1.25x)",
        1.25f,
        "युवा या दावत के शौकीन मेहमान",
        "Youth or guests with large appetite"
    );

    fun label(isHindi: Boolean): String = if (isHindi) labelHindi else labelEnglish
    fun note(isHindi: Boolean): String = if (isHindi) noteHindi else noteEnglish
}

@Keep
@Serializable
enum class DishCategory(
    val titleHindi: String,
    val titleEnglish: String
) {
    PANEER_SABZI("सब्ज़ी व पनीर", "Vegetables & Paneer"),
    DAL("दाल", "Dal & Lentils"),
    ROTI_POORI("रोटी व पूरी", "Roti & Breads"),
    RICE("चावल व पुलाव", "Rice & Pulao"),
    SWEETS("मिठाई व मीठा", "Sweets & Desserts"),
    RAITA_SALAD("रायता व सलाद", "Raita & Salad");

    fun title(isHindi: Boolean): String = if (isHindi) titleHindi else titleEnglish
}

@Keep
@Serializable
enum class IngredientCategory(
    val titleHindi: String,
    val titleEnglish: String,
    val colorHex: Long
) {
    SABZI_MANDI("सब्ज़ी मंडी (ताज़ा सब्ज़ियाँ)", "Vegetables (Mandi)", 0xFF38C792),
    KIRANA("किराना दुकान (अनाज, तेल व राशन)", "Grocery Store (Kirana)", 0xFFD97706),
    DAIRY("डेयरी उत्पाद (दूध, दही, पनीर व घी)", "Dairy Products", 0xFF2563EB),
    MASALA_DRY("मसाले व सूखे मेवे", "Spices & Dry Fruits", 0xFFDC2626);

    fun title(isHindi: Boolean): String = if (isHindi) titleHindi else titleEnglish
}

@Keep
@Serializable
data class RawIngredientRatio(
    val name: String,
    val category: IngredientCategory,
    val perPersonGrams: Float,
    val unit: String = "g", // "g" for grams, "ml" for millilitres, "pcs" for pieces
    val pricePerKgOrLitre: Float = 0f,
    val nameEnglish: String = ""
) {
    fun displayName(isHindi: Boolean): String =
        if (isHindi || nameEnglish.isBlank()) name else nameEnglish
}

@Keep
@Serializable
data class CateringDish(
    val id: String,
    val nameHindi: String,
    val englishName: String,
    val category: DishCategory,
    val description: String,
    val descriptionEnglish: String = "",
    val cookedServingPerPerson: Float,
    val servingUnit: String, // "ग्राम", "रोटी", "पूरी", "पीस", "मिली"
    val ingredients: List<RawIngredientRatio>,
    val chefTips: String = "",
    val popularWith: List<OccasionType> = listOf(OccasionType.HOME, OccasionType.PARTY, OccasionType.BHANDARA),
    val chefTipsEnglish: String = ""
) {
    val chefTip: String get() = chefTips

    fun displayName(isHindi: Boolean): String = if (isHindi) nameHindi else englishName

    fun displayDescription(isHindi: Boolean): String =
        if (isHindi || descriptionEnglish.isBlank()) description else descriptionEnglish

    fun displayChefTip(isHindi: Boolean): String =
        if (isHindi || chefTipsEnglish.isBlank()) chefTips else chefTipsEnglish

    fun displayUnit(isHindi: Boolean): String = if (isHindi) {
        servingUnit
    } else {
        when (servingUnit) {
            "ग्राम" -> "g"
            "मिली" -> "ml"
            "रोटी" -> "rotis"
            "पूरी" -> "puris"
            "पीस" -> "pcs"
            "कचौड़ी" -> "kachoris"
            "पापड़" -> "papad"
            else -> servingUnit
        }
    }
}

@Keep
@Serializable
data class GuestConfig(
    val occasion: OccasionType = OccasionType.PARTY,
    val adultsCount: Int = 15,
    val childrenCount: Int = 2,
    val appetite: AppetiteLevel = AppetiteLevel.NORMAL
) {
    val effectiveGuests: Float
        get() = ((adultsCount.coerceAtLeast(1)) + (childrenCount.coerceAtLeast(0) * 0.5f)) * appetite.multiplier
}

@Keep
@Serializable
data class CalculatedIngredientItem(
    val name: String,
    val category: IngredientCategory,
    val totalAmount: Float,
    val unit: String,
    val formattedQuantity: String,
    val estimatedCost: Float,
    val nameEnglish: String = ""
) {
    fun displayName(isHindi: Boolean): String =
        if (isHindi || nameEnglish.isBlank()) name else nameEnglish
}

@Keep
@Serializable
data class CalculatedDishRequirement(
    val dish: CateringDish,
    val totalCookedAmount: Float,
    val formattedCooked: String,
    val rawBreakdown: List<CalculatedIngredientItem>
)

@Keep
@Serializable
data class GroceryCategoryGroup(
    val category: IngredientCategory,
    val items: List<CalculatedIngredientItem>,
    val totalCategoryCost: Float
)

@Keep
@Serializable
data class CateringCalculationResult(
    val guestConfig: GuestConfig,
    val selectedDishes: List<CateringDish>,
    val dishRequirements: List<CalculatedDishRequirement>,
    val groceryGroups: List<GroceryCategoryGroup>,
    val estimatedCostMin: Int,
    val estimatedCostMax: Int,
    val costPerPlateMin: Int,
    val costPerPlateMax: Int
)
