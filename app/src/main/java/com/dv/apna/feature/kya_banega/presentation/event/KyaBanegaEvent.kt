package com.dv.apna.feature.kya_banega.presentation.event

import com.dv.apna.feature.kya_banega.domain.model.*
import com.dv.apna.feature.kya_banega.presentation.state.KitchenHubTab
import com.dv.apna.feature.kya_banega.presentation.state.RationStep

sealed interface KyaBanegaEvent {
    // Hub Tab
    data class OnSelectHubTab(val tab: KitchenHubTab) : KyaBanegaEvent

    // Kya Banega (Recipe Assistant) Events
    data class OnQueryChanged(val query: String) : KyaBanegaEvent
    data class OnAddIngredient(val ingredient: String) : KyaBanegaEvent
    data class OnRemoveIngredient(val ingredient: String) : KyaBanegaEvent
    data object OnClearAllIngredients : KyaBanegaEvent
    data class OnSelectMealMood(val mood: String?) : KyaBanegaEvent
    data object OnGenerateRecipes : KyaBanegaEvent
    data class OnSelectRecipeDetail(val recipe: RecipeModel?) : KyaBanegaEvent
    data object OnToggleSpeech : KyaBanegaEvent
    data object OnStopSpeech : KyaBanegaEvent
    data object OnResetToSearch : KyaBanegaEvent

    // Ration Calculator Events
    data class OnSelectOccasion(val occasion: OccasionType) : KyaBanegaEvent
    data class OnUpdateAdultsCount(val delta: Int) : KyaBanegaEvent
    data class OnUpdateChildrenCount(val delta: Int) : KyaBanegaEvent
    data class OnSetAdultsDirect(val count: Int) : KyaBanegaEvent
    data class OnSetChildrenDirect(val count: Int) : KyaBanegaEvent
    data class OnAdultsInputChanged(val input: String) : KyaBanegaEvent
    data class OnKidsInputChanged(val input: String) : KyaBanegaEvent
    data class OnSelectAppetite(val appetite: AppetiteLevel) : KyaBanegaEvent
    data class OnSelectDishCategory(val category: DishCategory) : KyaBanegaEvent
    data class OnToggleDish(val dishId: String) : KyaBanegaEvent
    data class OnApplyThaliPreset(val dishIds: List<String>) : KyaBanegaEvent
    data object OnNextRationStep : KyaBanegaEvent
    data object OnPreviousRationStep : KyaBanegaEvent
    data class OnGoToRationStep(val step: RationStep) : KyaBanegaEvent
    data object OnCalculateRation : KyaBanegaEvent
    data class OnToggleShoppingItem(val itemName: String) : KyaBanegaEvent
    data object OnResetRation : KyaBanegaEvent
}
