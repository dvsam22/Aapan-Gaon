package com.dv.apna.feature.kya_banega.domain.usecase

import com.dv.apna.core.common.Resource
import com.dv.apna.feature.kya_banega.domain.model.RecipeSuggestionResponse
import com.dv.apna.feature.kya_banega.domain.repository.KyaBanegaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GenerateRecipesUseCase @Inject constructor(
    private val repository: KyaBanegaRepository
) {
    operator fun invoke(
        ingredients: List<String>,
        mealType: String? = null
    ): Flow<Resource<RecipeSuggestionResponse>> {
        return repository.generateRecipes(ingredients, mealType)
    }
}
