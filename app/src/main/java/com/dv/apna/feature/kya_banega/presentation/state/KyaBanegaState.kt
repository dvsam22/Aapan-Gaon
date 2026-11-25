package com.dv.apna.feature.kya_banega.presentation.state

import com.dv.apna.feature.kya_banega.domain.model.*

enum class KitchenHubTab {
    RECIPES,
    RATION_CALCULATOR
}

enum class RationStep(val stepNumber: Int, val titleHindi: String) {
    OCCASION(1, "अवसर"),
    GUESTS(2, "मेहमान"),
    MENU(3, "मेनू"),
    QUANTITY(4, "मात्रा"),
    SHOPPING_LIST(5, "किराना पर्ची")
}

enum class RationValidationField {
    OCCASION,
    ADULTS,
    CHILDREN,
    APPETITE,
    MENU
}

data class RationCalculatorState(
    val currentStep: RationStep = RationStep.OCCASION,
    val selectedOccasion: OccasionType? = null,
    val selectedAppetite: AppetiteLevel? = null,
    val guestConfig: GuestConfig = GuestConfig(
        occasion = OccasionType.PARTY,
        adultsCount = 0,
        childrenCount = 0,
        appetite = AppetiteLevel.NORMAL
    ),
    val adultsInputText: String = "",
    val kidsInputText: String = "",
    val selectedCategory: DishCategory = DishCategory.PANEER_SABZI,
    val selectedDishIds: Set<String> = emptySet(),
    val calculationResult: CateringCalculationResult? = null,
    val checkedShoppingItems: Set<String> = emptySet(),
    val validationErrorField: RationValidationField? = null
)

data class KyaBanegaState(
    val currentTab: KitchenHubTab = KitchenHubTab.RECIPES,
    val queryInput: String = "",
    val selectedIngredients: List<String> = emptyList(),
    val selectedMealMood: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val recipes: List<RecipeModel> = emptyList(),
    val generalAdvice: String? = null,
    val selectedRecipeDetail: RecipeModel? = null,
    val isSpeaking: Boolean = false,
    val rationState: RationCalculatorState = RationCalculatorState()
)
