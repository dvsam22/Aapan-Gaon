package com.dv.apna.feature.kya_banega.presentation.viewmodel

import android.app.Application
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dv.apna.core.common.Resource
import com.dv.apna.core.datastore.PreferenceManager
import com.dv.apna.feature.kya_banega.data.datasource.CateringDatabase
import com.dv.apna.feature.kya_banega.domain.engine.CateringCalculatorEngine
import com.dv.apna.feature.kya_banega.domain.model.OccasionType
import com.dv.apna.feature.kya_banega.domain.model.RecipeModel
import com.dv.apna.feature.kya_banega.domain.model.stripStepPrefix
import com.dv.apna.feature.kya_banega.domain.usecase.GenerateRecipesUseCase
import com.dv.apna.feature.kya_banega.presentation.effect.KyaBanegaEffect
import com.dv.apna.feature.kya_banega.presentation.event.KyaBanegaEvent
import com.dv.apna.feature.kya_banega.presentation.state.KitchenHubTab
import com.dv.apna.feature.kya_banega.presentation.state.KyaBanegaState
import com.dv.apna.feature.kya_banega.presentation.state.RationCalculatorState
import com.dv.apna.feature.kya_banega.presentation.state.RationStep
import com.dv.apna.feature.kya_banega.presentation.state.RationValidationField
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class KyaBanegaViewModel @Inject constructor(
    application: Application,
    private val generateRecipesUseCase: GenerateRecipesUseCase,
    private val preferenceManager: PreferenceManager
) : AndroidViewModel(application), TextToSpeech.OnInitListener {

    private val _state = MutableStateFlow(KyaBanegaState())
    val state = _state.asStateFlow()

    private val _effect = Channel<KyaBanegaEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false
    private var currentLanguageCode: String = "en"

    init {
        viewModelScope.launch {
            preferenceManager.languageCode.collect { lang ->
                currentLanguageCode = lang ?: "en"
                if (_state.value.rationState.selectedDishIds.isNotEmpty()) {
                    recalculateRation()
                }
            }
        }
        viewModelScope.launch {
            // Defer TTS binder setup until after the 400ms screen slide transition completes.
            delay(450)
            initTtsIfNeeded()
        }
    }

    private fun initTtsIfNeeded() {
        if (tts != null) return
        try {
            tts = TextToSpeech(getApplication(), this)
        } catch (e: Exception) {
            android.util.Log.w("KyaBanegaVM", "TTS init error: ${e.message}")
        }
    }

    private fun isHindiLanguage(): Boolean = currentLanguageCode.equals("hi", ignoreCase = true)

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val isHindi = isHindiLanguage()
            val targetLocale = if (isHindi) Locale("hi", "IN") else Locale("en", "IN")
            val result = tts?.setLanguage(targetLocale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setLanguage(if (isHindi) Locale.getDefault() else Locale.US)
            }
            isTtsInitialized = true

            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _state.update { it.copy(isSpeaking = true) }
                }

                override fun onDone(utteranceId: String?) {
                    _state.update { it.copy(isSpeaking = false) }
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _state.update { it.copy(isSpeaking = false) }
                }
            })
        }
    }

    fun onEvent(event: KyaBanegaEvent) {
        when (event) {
            is KyaBanegaEvent.OnSelectHubTab -> {
                stopSpeech()
                _state.update { it.copy(currentTab = event.tab) }
            }

            // ==================== Kya Banega (Recipe Assistant) ====================
            is KyaBanegaEvent.OnQueryChanged -> {
                _state.update { it.copy(queryInput = event.query) }
            }
            is KyaBanegaEvent.OnAddIngredient -> {
                val clean = event.ingredient.trim()
                if (clean.isNotBlank() && !_state.value.selectedIngredients.contains(clean)) {
                    _state.update {
                        it.copy(
                            selectedIngredients = it.selectedIngredients + clean,
                            queryInput = ""
                        )
                    }
                }
            }
            is KyaBanegaEvent.OnRemoveIngredient -> {
                _state.update {
                    it.copy(
                        selectedIngredients = it.selectedIngredients - event.ingredient
                    )
                }
            }
            is KyaBanegaEvent.OnClearAllIngredients -> {
                _state.update {
                    it.copy(
                        selectedIngredients = emptyList(),
                        queryInput = ""
                    )
                }
            }
            is KyaBanegaEvent.OnSelectMealMood -> {
                _state.update {
                    it.copy(
                        selectedMealMood = if (it.selectedMealMood == event.mood) null else event.mood
                    )
                }
            }
            is KyaBanegaEvent.OnGenerateRecipes -> {
                generateRecipes()
            }
            is KyaBanegaEvent.OnSelectRecipeDetail -> {
                stopSpeech()
                _state.update { it.copy(selectedRecipeDetail = event.recipe) }
            }
            is KyaBanegaEvent.OnToggleSpeech -> {
                toggleSpeech()
            }
            is KyaBanegaEvent.OnStopSpeech -> {
                stopSpeech()
            }
            is KyaBanegaEvent.OnResetToSearch -> {
                stopSpeech()
                _state.update {
                    it.copy(
                        recipes = emptyList(),
                        selectedRecipeDetail = null,
                        error = null
                    )
                }
            }

            // ==================== Ration Calculator Events ====================
            is KyaBanegaEvent.OnSelectOccasion -> {
                _state.update { state ->
                    state.copy(
                        rationState = state.rationState.copy(
                            selectedOccasion = event.occasion,
                            guestConfig = state.rationState.guestConfig.copy(occasion = event.occasion),
                            validationErrorField = null
                        )
                    )
                }
                recalculateRation()
            }

            is KyaBanegaEvent.OnUpdateAdultsCount -> {
                val current = _state.value.rationState.guestConfig.adultsCount
                val updated = (current + event.delta).coerceIn(1, 3000)
                _state.update { state ->
                    state.copy(
                        rationState = state.rationState.copy(
                            guestConfig = state.rationState.guestConfig.copy(adultsCount = updated),
                            validationErrorField = null
                        )
                    )
                }
                recalculateRation()
            }

            is KyaBanegaEvent.OnUpdateChildrenCount -> {
                val current = _state.value.rationState.guestConfig.childrenCount
                val updated = (current + event.delta).coerceIn(0, 1000)
                _state.update { state ->
                    state.copy(
                        rationState = state.rationState.copy(
                            guestConfig = state.rationState.guestConfig.copy(childrenCount = updated),
                            validationErrorField = null
                        )
                    )
                }
                recalculateRation()
            }

            is KyaBanegaEvent.OnSetAdultsDirect -> {
                val updated = event.count.coerceIn(1, 3000)
                _state.update { state ->
                    state.copy(
                        rationState = state.rationState.copy(
                            guestConfig = state.rationState.guestConfig.copy(adultsCount = updated),
                            validationErrorField = null
                        )
                    )
                }
                recalculateRation()
            }

            is KyaBanegaEvent.OnSetChildrenDirect -> {
                val updated = event.count.coerceIn(0, 1000)
                _state.update { state ->
                    state.copy(
                        rationState = state.rationState.copy(
                            guestConfig = state.rationState.guestConfig.copy(childrenCount = updated),
                            validationErrorField = null
                        )
                    )
                }
                recalculateRation()
            }

            is KyaBanegaEvent.OnAdultsInputChanged -> {
                val sanitized = event.input.filter { it.isDigit() }.take(4)
                val parsed = sanitized.toIntOrNull()
                val finalInput = if (parsed != null && parsed > 3000) "3000" else sanitized
                val updatedCount = parsed?.coerceIn(0, 3000) ?: 0
                _state.update { state ->
                    state.copy(
                        rationState = state.rationState.copy(
                            adultsInputText = finalInput,
                            guestConfig = state.rationState.guestConfig.copy(adultsCount = updatedCount),
                            validationErrorField = null
                        )
                    )
                }
                recalculateRation()
            }

            is KyaBanegaEvent.OnKidsInputChanged -> {
                val sanitized = event.input.filter { it.isDigit() }.take(4)
                val parsed = sanitized.toIntOrNull()
                val finalInput = if (parsed != null && parsed > 1000) "1000" else sanitized
                val updatedCount = parsed?.coerceIn(0, 1000) ?: 0
                _state.update { state ->
                    state.copy(
                        rationState = state.rationState.copy(
                            kidsInputText = finalInput,
                            guestConfig = state.rationState.guestConfig.copy(childrenCount = updatedCount),
                            validationErrorField = null
                        )
                    )
                }
                recalculateRation()
            }

            is KyaBanegaEvent.OnSelectAppetite -> {
                _state.update { state ->
                    state.copy(
                        rationState = state.rationState.copy(
                            selectedAppetite = event.appetite,
                            guestConfig = state.rationState.guestConfig.copy(appetite = event.appetite),
                            validationErrorField = null
                        )
                    )
                }
                recalculateRation()
            }

            is KyaBanegaEvent.OnSelectDishCategory -> {
                _state.update { state ->
                    state.copy(
                        rationState = state.rationState.copy(selectedCategory = event.category)
                    )
                }
            }

            is KyaBanegaEvent.OnToggleDish -> {
                val currentSet = _state.value.rationState.selectedDishIds
                val newSet = if (currentSet.contains(event.dishId)) {
                    currentSet - event.dishId
                } else {
                    currentSet + event.dishId
                }
                _state.update { state ->
                    state.copy(
                        rationState = state.rationState.copy(
                            selectedDishIds = newSet,
                            validationErrorField = null
                        )
                    )
                }
                recalculateRation()
            }

            is KyaBanegaEvent.OnApplyThaliPreset -> {
                _state.update { state ->
                    state.copy(
                        rationState = state.rationState.copy(
                            selectedDishIds = event.dishIds.toSet(),
                            validationErrorField = null
                        )
                    )
                }
                recalculateRation()
            }

            is KyaBanegaEvent.OnNextRationStep -> {
                val currentStep = _state.value.rationState.currentStep
                if (!validateRationStep(currentStep)) return
                val next = when (currentStep) {
                    RationStep.OCCASION -> RationStep.GUESTS
                    RationStep.GUESTS -> RationStep.MENU
                    RationStep.MENU -> RationStep.QUANTITY
                    RationStep.QUANTITY -> RationStep.SHOPPING_LIST
                    RationStep.SHOPPING_LIST -> RationStep.SHOPPING_LIST
                }
                _state.update { state ->
                    state.copy(
                        rationState = state.rationState.copy(
                            currentStep = next,
                            validationErrorField = null
                        )
                    )
                }
                recalculateRation()
            }

            is KyaBanegaEvent.OnPreviousRationStep -> {
                val prev = when (_state.value.rationState.currentStep) {
                    RationStep.OCCASION -> RationStep.OCCASION
                    RationStep.GUESTS -> RationStep.OCCASION
                    RationStep.MENU -> RationStep.GUESTS
                    RationStep.QUANTITY -> RationStep.MENU
                    RationStep.SHOPPING_LIST -> RationStep.QUANTITY
                }
                _state.update { state ->
                    state.copy(
                        rationState = state.rationState.copy(
                            currentStep = prev,
                            validationErrorField = null
                        )
                    )
                }
            }

            is KyaBanegaEvent.OnGoToRationStep -> {
                // Step indicator clicks are disabled; transitions require forward/back validation.
            }

            is KyaBanegaEvent.OnCalculateRation -> {
                if (!validateRationStep(RationStep.MENU)) return
                recalculateRation()
                _state.update { state ->
                    state.copy(
                        rationState = state.rationState.copy(
                            currentStep = RationStep.QUANTITY,
                            validationErrorField = null
                        )
                    )
                }
            }

            is KyaBanegaEvent.OnToggleShoppingItem -> {
                val current = _state.value.rationState.checkedShoppingItems
                val updated = if (current.contains(event.itemName)) {
                    current - event.itemName
                } else {
                    current + event.itemName
                }
                _state.update { state ->
                    state.copy(rationState = state.rationState.copy(checkedShoppingItems = updated))
                }
            }

            is KyaBanegaEvent.OnResetRation -> {
                _state.update { state ->
                    state.copy(rationState = RationCalculatorState())
                }
                recalculateRation()
            }
        }
    }

    private fun validateRationStep(step: RationStep): Boolean {
        val rState = _state.value.rationState
        val adults = rState.adultsInputText.toIntOrNull()
        val kids = rState.kidsInputText.toIntOrNull()

        val failingField: RationValidationField? = when {
            rState.selectedOccasion == null -> RationValidationField.OCCASION

            step.stepNumber >= RationStep.GUESTS.stepNumber && (adults == null || adults < 1) ->
                RationValidationField.ADULTS

            step.stepNumber >= RationStep.GUESTS.stepNumber && (kids == null || kids < 0) ->
                RationValidationField.CHILDREN

            step.stepNumber >= RationStep.GUESTS.stepNumber && rState.selectedAppetite == null ->
                RationValidationField.APPETITE

            step.stepNumber >= RationStep.MENU.stepNumber && rState.selectedDishIds.isEmpty() ->
                RationValidationField.MENU

            step == RationStep.QUANTITY && rState.calculationResult?.dishRequirements.isNullOrEmpty() ->
                RationValidationField.MENU

            else -> null
        }

        if (failingField != null) {
            _state.update { state ->
                state.copy(rationState = state.rationState.copy(validationErrorField = failingField))
            }
            return false
        }
        return true
    }

    private fun recalculateRation() {
        val rState = _state.value.rationState
        val selectedDishes = CateringDatabase.allDishes.filter { rState.selectedDishIds.contains(it.id) }
        val result = CateringCalculatorEngine.calculate(
            guestConfig = rState.guestConfig,
            selectedDishes = selectedDishes,
            isHindi = isHindiLanguage()
        )
        _state.update { state ->
            state.copy(
                rationState = state.rationState.copy(calculationResult = result)
            )
        }
    }

    private fun generateRecipes() {
        val ingredients = _state.value.selectedIngredients
        if (ingredients.isEmpty()) {
            viewModelScope.launch {
                val isHindi = (preferenceManager.languageCode.firstOrNull() ?: currentLanguageCode)
                    .equals("hi", ignoreCase = true)
                val emptyMsg = if (isHindi) {
                    "कृपया कम से कम 1 सामग्री चुनें या लिखें"
                } else {
                    "Please select or type at least 1 ingredient"
                }
                _effect.send(KyaBanegaEffect.ShowToast(emptyMsg))
            }
            return
        }

        stopSpeech()
        viewModelScope.launch {
            val isHindi = (preferenceManager.languageCode.firstOrNull() ?: currentLanguageCode)
                .equals("hi", ignoreCase = true)
            generateRecipesUseCase(
                ingredients = ingredients,
                mealType = _state.value.selectedMealMood
            ).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _state.update {
                            it.copy(
                                isLoading = true,
                                error = null,
                                selectedRecipeDetail = null
                            )
                        }
                    }
                    is Resource.Success -> {
                        val response = resource.data
                        _state.update {
                            it.copy(
                                isLoading = false,
                                recipes = response?.recipes ?: emptyList(),
                                generalAdvice = response?.generalAdvice,
                                error = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        val fallbackErr = if (isHindi) {
                            "रेसिपी प्राप्त नहीं हो सकी"
                        } else {
                            "Could not fetch recipes"
                        }
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = resource.message ?: fallbackErr
                            )
                        }
                    }
                }
            }
        }
    }

    private fun toggleSpeech() {
        if (_state.value.isSpeaking) {
            stopSpeech()
            return
        }

        val recipe = _state.value.selectedRecipeDetail ?: return
        val isHindi = isHindiLanguage()
        if (tts == null) {
            initTtsIfNeeded()
        }
        if (!isTtsInitialized || tts == null) {
            viewModelScope.launch {
                val msg = if (isHindi) {
                    "आवाज़ की सुविधा अभी तैयार नहीं है"
                } else {
                    "Voice playback is not ready yet"
                }
                _effect.send(KyaBanegaEffect.ShowToast(msg))
            }
            return
        }

        val targetLocale = if (isHindi) Locale("hi", "IN") else Locale("en", "IN")
        val langResult = tts?.setLanguage(targetLocale)
        if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
            tts?.setLanguage(if (isHindi) Locale.getDefault() else Locale.US)
        }

        val speechText = buildString {
            if (isHindi) {
                append("${recipe.name} बनाने की विधि। ")
                append("लगने वाला समय: ${recipe.cookingTime}। ")
                recipe.steps.forEachIndexed { index, step ->
                    val cleanStep = step.stripStepPrefix()
                    if (cleanStep.isNotBlank()) {
                        append("चरण ${index + 1}: $cleanStep। ")
                    }
                }
                if (recipe.chefTip.isNotBlank()) {
                    append("खास शेफ टिप: ${recipe.chefTip}।")
                }
            } else {
                append("How to make ${recipe.name}. ")
                append("Cooking time: ${recipe.cookingTime}. ")
                recipe.steps.forEachIndexed { index, step ->
                    val cleanStep = step.stripStepPrefix()
                    if (cleanStep.isNotBlank()) {
                        append("Step ${index + 1}: $cleanStep. ")
                    }
                }
                if (recipe.chefTip.isNotBlank()) {
                    append("Chef secret tip: ${recipe.chefTip}.")
                }
            }
        }

        tts?.speak(speechText, TextToSpeech.QUEUE_FLUSH, null, "recipe_steps_utterance")
        _state.update { it.copy(isSpeaking = true) }
    }

    private fun stopSpeech() {
        tts?.stop()
        _state.update { it.copy(isSpeaking = false) }
    }

    override fun onCleared() {
        super.onCleared()
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (e: Exception) {
            android.util.Log.w("KyaBanegaVM", "TTS shutdown error: ${e.message}")
        }
    }
}
