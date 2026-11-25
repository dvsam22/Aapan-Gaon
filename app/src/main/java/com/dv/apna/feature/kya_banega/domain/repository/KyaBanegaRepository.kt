package com.dv.apna.feature.kya_banega.domain.repository

import com.dv.apna.core.common.Resource
import com.dv.apna.feature.kya_banega.domain.model.RecipeSuggestionResponse
import kotlinx.coroutines.flow.Flow

interface KyaBanegaRepository {
    fun generateRecipes(
        ingredients: List<String>,
        mealType: String? = null
    ): Flow<Resource<RecipeSuggestionResponse>>
}
