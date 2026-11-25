package com.dv.apna.feature.kya_banega.presentation.screen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.dv.apna.R
import com.dv.apna.core.ads.BannerAdView
import com.dv.apna.core.components.shimmerEffect
import com.dv.apna.core.config.RemoteConfigManager
import com.dv.apna.core.theme.MintGradientEnd
import com.dv.apna.core.theme.MintGradientMiddle
import com.dv.apna.core.theme.MintGradientStart
import com.dv.apna.core.utils.sdp
import com.dv.apna.core.utils.ssp
import com.dv.apna.feature.kya_banega.domain.model.RecipeModel
import com.dv.apna.feature.kya_banega.domain.model.stripStepPrefix
import com.dv.apna.feature.kya_banega.presentation.effect.KyaBanegaEffect
import com.dv.apna.feature.kya_banega.presentation.event.KyaBanegaEvent
import com.dv.apna.feature.kya_banega.presentation.state.KitchenHubTab
import com.dv.apna.feature.kya_banega.presentation.state.KyaBanegaState
import com.dv.apna.feature.kya_banega.presentation.state.RationStep
import com.dv.apna.feature.kya_banega.presentation.util.KyaBanegaShareHelper
import com.dv.apna.feature.kya_banega.presentation.util.RationShareHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import java.util.Locale

private val BrandGreen = Color(0xFF38C792)
private val BrandGreenDark = Color(0xFF1B6A4C)
private val LightGreenBg = Color(0xFFEFFAF6)
private val CardBorderColor = Color(0xFFD9D9D9)
private val TextMain = Color(0xFF1E232C)
private val TextSecondary = Color(0xFF64748B)

@Composable
fun KyaBanegaScreen(
    state: KyaBanegaState,
    onEvent: (KyaBanegaEvent) -> Unit,
    effect: Flow<KyaBanegaEffect>,
    remoteConfigManager: RemoteConfigManager,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val currentContext by rememberUpdatedState(context)

    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenTexts = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = spokenTexts?.firstOrNull()?.trim()
            if (!spokenText.isNullOrBlank()) {
                val splitItems = spokenText.split(Regex("[,\\s]+"))
                    .map { it.trim(',', '.', ' ') }
                    .filter {
                        it.isNotBlank() &&
                            it.length > 1 &&
                            !it.equals("and", ignoreCase = true) &&
                            !it.equals("aur", ignoreCase = true) &&
                            it != "और"
                    }
                    .map { item ->
                        item.replaceFirstChar { ch ->
                            if (ch.isLowerCase()) ch.titlecase(Locale.ROOT) else ch.toString()
                        }
                    }

                if (splitItems.isNotEmpty()) {
                    splitItems.forEach { onEvent(KyaBanegaEvent.OnAddIngredient(it)) }
                } else {
                    onEvent(KyaBanegaEvent.OnAddIngredient(spokenText))
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        effect.collectLatest { eff ->
            when (eff) {
                is KyaBanegaEffect.ShowToast -> {
                    Toast.makeText(currentContext, eff.message, Toast.LENGTH_SHORT).show()
                }
                is KyaBanegaEffect.LaunchVoiceInput -> {
                    launchVoiceRecognition(
                        context = currentContext,
                        onLaunchIntent = speechRecognizerLauncher::launch,
                        prompt = eff.prompt?.takeIf { it.isNotBlank() }
                            ?: currentContext.getString(R.string.kya_banega_mic_prompt)
                    )
                }
            }
        }
    }

    // Graceful back handling
    val handleBack: () -> Unit = {
        when {
            state.selectedRecipeDetail != null -> {
                onEvent(KyaBanegaEvent.OnSelectRecipeDetail(null))
            }
            state.recipes.isNotEmpty() -> {
                onEvent(KyaBanegaEvent.OnResetToSearch)
            }
            state.currentTab == KitchenHubTab.RATION_CALCULATOR -> {
                when (state.rationState.currentStep) {
                    RationStep.OCCASION -> onNavigateBack()
                    else -> onEvent(KyaBanegaEvent.OnPreviousRationStep)
                }
            }
            else -> onNavigateBack()
        }
    }

    BackHandler(onBack = handleBack)

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(MintGradientStart, MintGradientMiddle, MintGradientEnd)))
    ) {
        val (bottomImage, mainContent) = createRefs()

        // App Standard Bottom Decoration
        Image(
            painter = painterResource(id = R.drawable.iv_bottomview),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(bottomImage) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            contentScale = ContentScale.FillWidth
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .constrainAs(mainContent) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            // App-Standard Header Bar
            KyaBanegaAppTopBar(
                title = when {
                    state.selectedRecipeDetail != null -> stringResource(id = R.string.kya_banega_detail_title)
                    state.recipes.isNotEmpty() -> stringResource(id = R.string.kya_banega_recipes_title)
                    state.currentTab == KitchenHubTab.RATION_CALCULATOR -> {
                        when (state.rationState.currentStep) {
                            RationStep.OCCASION -> stringResource(id = R.string.ration_step1_top_title)
                            RationStep.GUESTS -> stringResource(id = R.string.ration_step2_top_title)
                            RationStep.MENU -> stringResource(id = R.string.ration_step3_top_title)
                            RationStep.QUANTITY -> stringResource(id = R.string.ration_step4_top_title)
                            RationStep.SHOPPING_LIST -> stringResource(id = R.string.ration_step5_top_title)
                        }
                    }
                    else -> stringResource(id = R.string.kya_banega_title)
                },
                onBackClick = handleBack
            )

            // Kitchen Hub Tab Switcher (Visible on Main Screens when not loading)
            if (!state.isLoading && state.selectedRecipeDetail == null && state.recipes.isEmpty()) {
                KitchenHubTabBar(
                    selectedTab = state.currentTab,
                    onTabSelected = { onEvent(KyaBanegaEvent.OnSelectHubTab(it)) }
                )
            }

            when {
                state.isLoading -> {
                    KyaBanegaRecipesSkeleton()
                }
                state.selectedRecipeDetail != null -> {
                    RecipeDetailView(
                        recipe = state.selectedRecipeDetail,
                        isSpeaking = state.isSpeaking,
                        onToggleSpeech = { onEvent(KyaBanegaEvent.OnToggleSpeech) },
                        onShare = {
                            val isCurrentHindi = context.resources.configuration.locales[0].language.equals("hi", ignoreCase = true)
                            val text = KyaBanegaShareHelper.formatRecipeShareText(
                                recipe = state.selectedRecipeDetail,
                                packageName = context.packageName,
                                isHindi = isCurrentHindi
                            )
                            KyaBanegaShareHelper.shareText(context, text, isHindi = isCurrentHindi)
                        },
                        remoteConfigManager = remoteConfigManager
                    )
                }
                state.recipes.isNotEmpty() -> {
                    RecipeResultsView(
                        recipes = state.recipes,
                        generalAdvice = state.generalAdvice,
                        onSelectRecipe = { onEvent(KyaBanegaEvent.OnSelectRecipeDetail(it)) },
                        onResetSearch = { onEvent(KyaBanegaEvent.OnResetToSearch) },
                        remoteConfigManager = remoteConfigManager
                    )
                }
                else -> {
                    AnimatedContent(
                        targetState = state.currentTab,
                        transitionSpec = {
                            if (targetState == KitchenHubTab.RATION_CALCULATOR) {
                                (slideInHorizontally(animationSpec = tween(280)) { width -> width } +
                                    fadeIn(animationSpec = tween(280)))
                                    .togetherWith(
                                        slideOutHorizontally(animationSpec = tween(280)) { width -> -width } +
                                            fadeOut(animationSpec = tween(280))
                                    )
                            } else {
                                (slideInHorizontally(animationSpec = tween(280)) { width -> -width } +
                                    fadeIn(animationSpec = tween(280)))
                                    .togetherWith(
                                        slideOutHorizontally(animationSpec = tween(280)) { width -> width } +
                                            fadeOut(animationSpec = tween(280))
                                    )
                            }
                        },
                        label = "HubTabContentTransition",
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) { targetTab ->
                        when (targetTab) {
                            KitchenHubTab.RATION_CALCULATOR -> {
                                Column(modifier = Modifier.fillMaxSize()) {
                                    // Ration Calculator 5-Step Flow (Visual Progress Only)
                                    RationStepIndicator(
                                        currentStep = state.rationState.currentStep
                                    )

                                    when (state.rationState.currentStep) {
                                        RationStep.OCCASION -> {
                                            RationOccasionStepView(
                                                selectedOccasion = state.rationState.selectedOccasion,
                                                validationErrorField = state.rationState.validationErrorField,
                                                onSelectOccasion = { onEvent(KyaBanegaEvent.OnSelectOccasion(it)) },
                                                onNext = { onEvent(KyaBanegaEvent.OnNextRationStep) }
                                            )
                                        }
                                        RationStep.GUESTS -> {
                                            RationGuestsStepView(
                                                adultsInputText = state.rationState.adultsInputText,
                                                kidsInputText = state.rationState.kidsInputText,
                                                selectedAppetite = state.rationState.selectedAppetite,
                                                validationErrorField = state.rationState.validationErrorField,
                                                onAdultsInputChange = { onEvent(KyaBanegaEvent.OnAdultsInputChanged(it)) },
                                                onKidsInputChange = { onEvent(KyaBanegaEvent.OnKidsInputChanged(it)) },
                                                onSelectAppetite = { onEvent(KyaBanegaEvent.OnSelectAppetite(it)) },
                                                onPrevious = { onEvent(KyaBanegaEvent.OnPreviousRationStep) },
                                                onNext = { onEvent(KyaBanegaEvent.OnNextRationStep) }
                                            )
                                        }
                                        RationStep.MENU -> {
                                            RationMenuBuilderStepView(
                                                selectedCategory = state.rationState.selectedCategory,
                                                selectedDishIds = state.rationState.selectedDishIds,
                                                validationErrorField = state.rationState.validationErrorField,
                                                onSelectCategory = { onEvent(KyaBanegaEvent.OnSelectDishCategory(it)) },
                                                onToggleDish = { onEvent(KyaBanegaEvent.OnToggleDish(it)) },
                                                onPrevious = { onEvent(KyaBanegaEvent.OnPreviousRationStep) },
                                                onCalculate = { onEvent(KyaBanegaEvent.OnCalculateRation) }
                                            )
                                        }
                                        RationStep.QUANTITY -> {
                                            state.rationState.calculationResult?.let { result ->
                                                RationQuantityStepView(
                                                    result = result,
                                                    onPrevious = { onEvent(KyaBanegaEvent.OnPreviousRationStep) },
                                                    onNext = { onEvent(KyaBanegaEvent.OnNextRationStep) },
                                                    remoteConfigManager = remoteConfigManager
                                                )
                                            }
                                        }
                                        RationStep.SHOPPING_LIST -> {
                                            state.rationState.calculationResult?.let { result ->
                                                RationShoppingListStepView(
                                                    result = result,
                                                    checkedItems = state.rationState.checkedShoppingItems,
                                                    onToggleItem = { onEvent(KyaBanegaEvent.OnToggleShoppingItem(it)) },
                                                    onReset = { onEvent(KyaBanegaEvent.OnResetRation) },
                                                    onShareWhatsApp = {
                                                        val isCurrentHindi = context.resources.configuration.locales[0].language.equals("hi", ignoreCase = true)
                                                        val shareText = RationShareHelper.formatRationShareText(
                                                            result = result,
                                                            packageName = context.packageName,
                                                            isHindi = isCurrentHindi,
                                                            checkedItems = state.rationState.checkedShoppingItems
                                                        )
                                                        RationShareHelper.shareText(context, shareText, isHindi = isCurrentHindi)
                                                    },
                                                    remoteConfigManager = remoteConfigManager
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            KitchenHubTab.RECIPES -> {
                                IngredientSelectionView(
                                    queryInput = state.queryInput,
                                    selectedIngredients = state.selectedIngredients,
                                    selectedMealMood = state.selectedMealMood,
                                    errorMessage = state.error,
                                    onQueryChanged = { onEvent(KyaBanegaEvent.OnQueryChanged(it)) },
                                    onAddIngredient = { onEvent(KyaBanegaEvent.OnAddIngredient(it)) },
                                    onRemoveIngredient = { onEvent(KyaBanegaEvent.OnRemoveIngredient(it)) },
                                    onClearAll = { onEvent(KyaBanegaEvent.OnClearAllIngredients) },
                                    onSelectMood = { onEvent(KyaBanegaEvent.OnSelectMealMood(it)) },
                                    onVoiceClick = {
                                        launchVoiceRecognition(
                                            context = context,
                                            onLaunchIntent = speechRecognizerLauncher::launch,
                                            prompt = context.getString(R.string.kya_banega_mic_prompt)
                                        )
                                    },
                                    onGenerateClick = { onEvent(KyaBanegaEvent.OnGenerateRecipes) },
                                    remoteConfigManager = remoteConfigManager
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Launches Android speech recognition configured to match the current app language ("hi-IN" or "en-IN").
 */
private fun launchVoiceRecognition(
    context: Context,
    onLaunchIntent: (Intent) -> Unit,
    prompt: String
) {
    val isHindi = context.resources.configuration.locales[0].language.equals("hi", ignoreCase = true)
    val speechLanguage = if (isHindi) "hi-IN" else "en-IN"
    try {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, speechLanguage)
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
            putExtra(RecognizerIntent.EXTRA_PROMPT, prompt)
        }
        onLaunchIntent(intent)
    } catch (_: Exception) {
        Toast.makeText(
            context,
            context.getString(R.string.kya_banega_voice_not_supported),
            Toast.LENGTH_SHORT
        ).show()
    }
}

/**
 * Standard App TopBar matching CropDoctor, LegalPanchayat, FamilyFunction, etc.
 */
@Composable
fun KyaBanegaAppTopBar(
    title: String,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.sdp(), vertical = 14.sdp()),
        contentAlignment = Alignment.CenterStart
    ) {
        Surface(
            modifier = Modifier
                .width(34.sdp())
                .height(46.sdp())
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onBackClick() },
            shape = RoundedCornerShape(24.sdp()),
            color = LightGreenBg,
            border = BorderStroke(1.sdp(), CardBorderColor)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_left),
                    contentDescription = stringResource(id = R.string.back),
                    modifier = Modifier.size(20.sdp()),
                    tint = Color.Black
                )
            }
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.ssp(),
                color = TextMain
            ),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 46.sdp()),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun IngredientSelectionView(
    queryInput: String,
    selectedIngredients: List<String>,
    selectedMealMood: String?,
    errorMessage: String?,
    onQueryChanged: (String) -> Unit,
    onAddIngredient: (String) -> Unit,
    onRemoveIngredient: (String) -> Unit,
    onClearAll: () -> Unit,
    onSelectMood: (String?) -> Unit,
    onVoiceClick: () -> Unit,
    onGenerateClick: () -> Unit,
    remoteConfigManager: RemoteConfigManager
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val isHindi = context.resources.configuration.locales[0].language == "hi"
    var showBannerAd by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // Defer AdView inflation until after the 400ms NavHost slide animation completes.
        delay(450)
        showBannerAd = true
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(start = 16.sdp(), end = 16.sdp(), top = 2.sdp(), bottom = 16.sdp()),
            verticalArrangement = Arrangement.spacedBy(14.sdp())
        ) {
        // App-Standard Search & Voice Bar
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.sdp())
                    .background(Color.White, CircleShape)
                    .border(width = 1.5.dp, color = BrandGreen, shape = CircleShape)
                    .padding(horizontal = 12.sdp()),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = BrandGreen,
                        modifier = Modifier.size(20.sdp())
                    )

                    Spacer(modifier = Modifier.width(8.sdp()))

                    Box(modifier = Modifier.weight(1f)) {
                        if (queryInput.isEmpty()) {
                            Text(
                                text = stringResource(id = R.string.kya_banega_search_hint),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 12.ssp(),
                                    color = Color(0xFF8391A1)
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        BasicTextField(
                            value = queryInput,
                            onValueChange = onQueryChanged,
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = TextStyle(
                                fontSize = 13.ssp(),
                                color = TextMain
                            ),
                            singleLine = true,
                            cursorBrush = SolidColor(BrandGreen),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    if (queryInput.isNotBlank()) {
                                        onAddIngredient(queryInput)
                                        keyboardController?.hide()
                                    }
                                }
                            )
                        )
                    }

                    if (queryInput.isNotBlank()) {
                        Surface(
                            shape = CircleShape,
                            color = BrandGreen,
                            modifier = Modifier
                                .size(28.sdp())
                                .clickable {
                                    onAddIngredient(queryInput)
                                    keyboardController?.hide()
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.sdp())
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(6.sdp()))
                    }

                    // Voice Input Button
                    Surface(
                        modifier = Modifier
                            .size(32.sdp())
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onVoiceClick() },
                        shape = CircleShape,
                        color = LightGreenBg
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_mic_voice),
                                contentDescription = "Voice",
                                tint = BrandGreen,
                                modifier = Modifier.size(17.sdp())
                            )
                        }
                    }
                }
            }
        }

        // Selected Ingredients (Your Ingredients) Chips Section
        if (selectedIngredients.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.sdp()),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.sdp())
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.sdp())
                            .background(LightGreenBg, RoundedCornerShape(8.sdp()))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.sdp())
                        ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.sdp())
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_chef_hat),
                                    contentDescription = null,
                                    tint = BrandGreenDark,
                                    modifier = Modifier.size(16.sdp())
                                )
                                Text(
                                    text = stringResource(id = R.string.kya_banega_selected_count, selectedIngredients.size),
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.ssp(),
                                        color = BrandGreenDark
                                    )
                                )
                            }

                            Text(
                                text = stringResource(id = R.string.kya_banega_clear_all),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.ssp(),
                                    color = Color(0xFFDC2626)
                                ),
                                modifier = Modifier.clickable { onClearAll() }
                            )
                        }

                        Spacer(modifier = Modifier.height(10.sdp()))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.sdp()),
                            verticalArrangement = Arrangement.spacedBy(6.sdp())
                        ) {
                            selectedIngredients.forEach { item ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.sdp()))
                                        .background(BrandGreen)
                                        .clickable { onRemoveIngredient(item) }
                                        .padding(start = 12.sdp(), end = 6.sdp(), top = 5.sdp(), bottom = 5.sdp())
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.sdp())
                                    ) {
                                        Text(
                                            text = item,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 12.ssp(),
                                                color = Color.White
                                            )
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(18.sdp())
                                                .background(Color.White, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Remove",
                                                tint = BrandGreen,
                                                modifier = Modifier.size(11.sdp())
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        }

        // Meal Mood Selector (Clean 2x2 Grid - Never Cut Off!)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.sdp())) {
                Text(
                    text = stringResource(id = R.string.kya_banega_mood_question),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.ssp(),
                        color = TextMain
                    )
                )

                val moods = listOf(
                    Pair(null, stringResource(id = R.string.kya_banega_mood_all)),
                    Pair("quick", stringResource(id = R.string.kya_banega_mood_quick)),
                    Pair("lunch", stringResource(id = R.string.kya_banega_mood_lunch)),
                    Pair("snacks", stringResource(id = R.string.kya_banega_mood_snacks))
                )

                // Row 1 (Equal 50% split)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.sdp())
                ) {
                    MoodPillButton(
                        text = moods[0].second,
                        isSelected = selectedMealMood == moods[0].first,
                        onClick = { onSelectMood(moods[0].first) },
                        modifier = Modifier.weight(1f)
                    )
                    MoodPillButton(
                        text = moods[1].second,
                        isSelected = selectedMealMood == moods[1].first,
                        onClick = { onSelectMood(moods[1].first) },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 2 (Equal 50% split)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.sdp())
                ) {
                    MoodPillButton(
                        text = moods[2].second,
                        isSelected = selectedMealMood == moods[2].first,
                        onClick = { onSelectMood(moods[2].first) },
                        modifier = Modifier.weight(1f)
                    )
                    MoodPillButton(
                        text = moods[3].second,
                        isSelected = selectedMealMood == moods[3].first,
                        onClick = { onSelectMood(moods[3].first) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Error message if any
        if (errorMessage != null) {
            item {
                Surface(
                    shape = RoundedCornerShape(8.sdp()),
                    color = Color(0xFFFFEBEE),
                    border = BorderStroke(1.sdp(), Color(0xFFFCA5A5)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFFC62828),
                            fontSize = 12.ssp()
                        ),
                        modifier = Modifier.padding(10.sdp())
                    )
                }
            }
        }

        // Category 1: Daily Vegetables
        item {
            AttractiveIngredientCard(
                categoryTitle = stringResource(id = R.string.kya_banega_cat_veggies_header),
                items = if (isHindi) {
                    listOf(
                        "आलू", "प्याज़", "टमाटर", "लौकी", "हरी मिर्च", "पालक",
                        "मटर", "गोभी", "बैंगन", "भिंडी", "कद्दू", "शिमला मिर्च", "लहसुन-अदरक"
                    )
                } else {
                    listOf(
                        "Potato", "Onion", "Tomato", "Bottle Gourd", "Green Chilli", "Spinach",
                        "Peas", "Cauliflower", "Brinjal", "Ladyfinger", "Pumpkin", "Capsicum", "Garlic-Ginger"
                    )
                },
                selectedList = selectedIngredients,
                onItemClick = onAddIngredient,
                headerBg = Color(0xFFE8F5E9),
                headerIconTint = Color(0xFF2E7D32)
            )
        }

        // Category 2: Dal, Besan & Grains
        item {
            AttractiveIngredientCard(
                categoryTitle = stringResource(id = R.string.kya_banega_cat_grains_header),
                items = if (isHindi) {
                    listOf(
                        "बेसन", "सूजी", "आटा", "चावल", "मूंग दाल", "चना दाल",
                        "अरहर दाल", "पोहा", "मैदा", "दलिया"
                    )
                } else {
                    listOf(
                        "Gram Flour (Besan)", "Semolina (Sooji)", "Wheat Flour (Atta)", "Rice", "Moong Dal", "Chana Dal",
                        "Toor Dal", "Poha", "Maida", "Broken Wheat"
                    )
                },
                selectedList = selectedIngredients,
                onItemClick = onAddIngredient,
                headerBg = Color(0xFFFFF8E1),
                headerIconTint = Color(0xFFF57F17)
            )
        }

        // Category 3: Dairy & Extras
        item {
            AttractiveIngredientCard(
                categoryTitle = stringResource(id = R.string.kya_banega_cat_dairy_header),
                items = if (isHindi) {
                    listOf(
                        "दही", "दूध", "पनीर", "मलाई", "घी / मक्खन", "ब्रेड"
                    )
                } else {
                    listOf(
                        "Curd (Dahi)", "Milk", "Paneer", "Fresh Cream", "Ghee / Butter", "Bread"
                    )
                },
                selectedList = selectedIngredients,
                onItemClick = onAddIngredient,
                headerBg = Color(0xFFE3F2FD),
                headerIconTint = Color(0xFF1976D2)
            )
        }

        // Category 4: Leftover Magic
        item {
            AttractiveIngredientCard(
                categoryTitle = stringResource(id = R.string.kya_banega_cat_leftover_header),
                items = if (isHindi) {
                    listOf(
                        "बची हुई रोटी", "बचा हुआ चावल", "बची हुई दाल", "उबले आलू"
                    )
                } else {
                    listOf(
                        "Leftover Roti", "Leftover Rice", "Leftover Dal", "Boiled Potatoes"
                    )
                },
                selectedList = selectedIngredients,
                onItemClick = onAddIngredient,
                headerBg = Color(0xFFFFF3E0),
                headerIconTint = Color(0xFFE65100)
            )
        }

            item {
                if (showBannerAd) {
                    BannerAdView(remoteConfigManager = remoteConfigManager)
                } else {
                    Spacer(modifier = Modifier.height(58.sdp()))
                }
            }
        }

        // Fixed Bottom Primary CTA Button (Always visible without scrolling)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.sdp(), vertical = 10.sdp()),
            contentAlignment = Alignment.Center
        ) {
            KyaBanegaPillCtaButton(
                text = stringResource(
                    id = R.string.kya_banega_btn_generate,
                    selectedIngredients.size
                ),
                onClick = onGenerateClick,
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_cooking_pot),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.sdp())
                    )
                }
            )
        }
    }
}

/**
 * Clean & Crisp Category Card (No ugly blurry shadows, clean modern pills)
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AttractiveIngredientCard(
    categoryTitle: String,
    items: List<String>,
    selectedList: List<String>,
    onItemClick: (String) -> Unit,
    headerBg: Color,
    headerIconTint: Color
) {
    val selectedSet = remember(selectedList) { selectedList.toSet() }
    val cardShape = RoundedCornerShape(12.sdp())
    val innerShape = RoundedCornerShape(8.sdp())
    val chipShape = RoundedCornerShape(20.sdp())
    val badgeSize = 18.sdp()
    val iconSize = 11.sdp()
    val borderStrokeWidth = 1.sdp()
    val unselectedBorderColor = Color(0xFFDCE2E6)
    val unselectedBadgeColor = Color(0xFFE2E8F0)
    val unselectedTextColor = Color(0xFF334155)
    val unselectedIconTint = Color(0xFF64748B)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.sdp())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.sdp())
                .background(LightGreenBg, innerShape)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.sdp())
            ) {
            // Header Strip with Color Accent
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.sdp()),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.sdp())
            ) {
                Box(
                    modifier = Modifier
                        .size(28.sdp())
                        .background(headerBg, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_cooking_pot),
                        contentDescription = null,
                        modifier = Modifier.size(15.sdp()),
                        tint = headerIconTint
                    )
                }

                Text(
                    text = categoryTitle,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.ssp(),
                        color = TextMain
                    )
                )
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.sdp()),
                verticalArrangement = Arrangement.spacedBy(6.sdp())
            ) {
                items.forEach { name ->
                    val isSelected = selectedSet.contains(name)
                    Box(
                        modifier = Modifier
                            .clip(chipShape)
                            .background(if (isSelected) BrandGreen else Color.White)
                            .border(
                                borderStrokeWidth,
                                if (isSelected) BrandGreen else unselectedBorderColor,
                                chipShape
                            )
                            .clickable { onItemClick(name) }
                            .padding(start = 12.sdp(), end = 6.sdp(), top = 5.sdp(), bottom = 5.sdp())
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.sdp())
                        ) {
                            Text(
                                text = name,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                    fontSize = 12.ssp(),
                                    color = if (isSelected) Color.White else unselectedTextColor
                                )
                            )
                            if (isSelected) {
                                // Modern Circular Check Badge
                                Box(
                                    modifier = Modifier
                                        .size(badgeSize)
                                        .background(Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = BrandGreen,
                                        modifier = Modifier.size(iconSize)
                                    )
                                }
                            } else {
                                // Modern Circular Add Badge
                                Box(
                                    modifier = Modifier
                                        .size(badgeSize)
                                        .background(unselectedBadgeColor, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add",
                                        tint = unselectedIconTint,
                                        modifier = Modifier.size(iconSize)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
}

/**
 * Recipes Shimmer Skeleton Loader (Full Screen Modern Skeleton)
 */
@Composable
fun KyaBanegaRecipesSkeleton() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.sdp(), end = 16.sdp(), top = 8.sdp(), bottom = 80.sdp()),
        verticalArrangement = Arrangement.spacedBy(14.sdp()),
        userScrollEnabled = false
    ) {
        // Top AI status banner
        item {
            ChefNoteHighlightCard(
                title = stringResource(id = R.string.kya_banega_loading_title),
                message = stringResource(id = R.string.kya_banega_loading_desc)
            )
        }

        // Section Title Placeholder
        item {
            Box(
                modifier = Modifier
                    .width(130.sdp())
                    .height(18.sdp())
                    .clip(RoundedCornerShape(4.sdp()))
                    .shimmerEffect()
            )
        }

        // 3 Recipe Card Skeletons
        repeat(3) {
            item {
                RecipeCardSkeleton()
            }
        }
    }
}

@Composable
private fun RecipeCardSkeleton() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.sdp()),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.sdp())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.sdp())
                .background(LightGreenBg, RoundedCornerShape(8.sdp()))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.sdp()),
                verticalArrangement = Arrangement.spacedBy(10.sdp())
            ) {
                // Title and category badge row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.55f)
                            .height(16.sdp())
                            .clip(RoundedCornerShape(4.sdp()))
                            .shimmerEffect()
                    )
                    Box(
                        modifier = Modifier
                            .width(56.sdp())
                            .height(18.sdp())
                            .clip(RoundedCornerShape(6.sdp()))
                            .shimmerEffect()
                    )
                }

                // Subtitle description line
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.82f)
                        .height(12.sdp())
                        .clip(RoundedCornerShape(4.sdp()))
                        .shimmerEffect()
                )

                HorizontalDivider(thickness = 1.sdp(), color = Color(0xFFDDE7E3))

                // Bottom row: Time, Servings and Button placeholder
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.sdp()),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(50.sdp())
                                .height(14.sdp())
                                .clip(RoundedCornerShape(4.sdp()))
                                .shimmerEffect()
                        )
                        Box(
                            modifier = Modifier
                                .width(46.sdp())
                                .height(14.sdp())
                                .clip(RoundedCornerShape(4.sdp()))
                                .shimmerEffect()
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(76.sdp())
                            .height(24.sdp())
                            .clip(RoundedCornerShape(14.sdp()))
                            .shimmerEffect()
                    )
                }
            }
        }
    }
}

/**
 * Recipe Results View (Matching user's mockup & App card style)
 */
@Composable
fun RecipeResultsView(
    recipes: List<RecipeModel>,
    generalAdvice: String?,
    onSelectRecipe: (RecipeModel) -> Unit,
    onResetSearch: () -> Unit,
    remoteConfigManager: RemoteConfigManager
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.sdp(), end = 16.sdp(), top = 8.sdp(), bottom = 80.sdp()),
        verticalArrangement = Arrangement.spacedBy(14.sdp())
    ) {
        if (!generalAdvice.isNullOrBlank()) {
            item {
                ChefNoteHighlightCard(
                    title = stringResource(id = R.string.kya_banega_chef_note_title),
                    message = generalAdvice
                )
            }
        }

        item {
            Text(
                text = stringResource(id = R.string.kya_banega_recipes_found),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.ssp(),
                    color = TextMain
                )
            )
        }

        items(recipes) { recipe ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectRecipe(recipe) },
                shape = RoundedCornerShape(12.sdp()),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.sdp())
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.sdp())
                        .background(LightGreenBg, RoundedCornerShape(8.sdp()))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.sdp()),
                        verticalArrangement = Arrangement.spacedBy(8.sdp())
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = recipe.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.ssp(),
                                    color = TextMain
                                ),
                                modifier = Modifier.weight(1f)
                            )

                            Surface(
                                shape = RoundedCornerShape(6.sdp()),
                                color = Color.White,
                                border = BorderStroke(1.sdp(), Color(0x3338C792))
                            ) {
                                Text(
                                    text = recipe.category,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.ssp(),
                                        color = BrandGreenDark
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.sdp(), vertical = 3.sdp()),
                                    maxLines = 1
                                )
                            }
                        }

                        if (recipe.subtitle.isNotBlank()) {
                            Text(
                                text = recipe.subtitle,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.ssp(),
                                    color = TextSecondary
                                )
                            )
                        }

                        HorizontalDivider(thickness = 1.sdp(), color = Color(0xFFDDE7E3))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.sdp()),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(10.sdp()),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.sdp())
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_timer_clock),
                                        contentDescription = null,
                                        modifier = Modifier.size(13.sdp()),
                                        tint = TextSecondary
                                    )
                                    Text(
                                        text = recipe.cookingTime,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 11.ssp(),
                                            color = TextSecondary
                                        ),
                                        maxLines = 1,
                                        softWrap = false,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Row(
                                    modifier = Modifier.weight(1f, fill = false),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.sdp())
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_servings_people),
                                        contentDescription = null,
                                        modifier = Modifier.size(13.sdp()),
                                        tint = TextSecondary
                                    )
                                    Text(
                                        text = recipe.servings,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 11.ssp(),
                                            color = TextSecondary
                                        ),
                                        maxLines = 1,
                                        softWrap = false,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(14.sdp()),
                                color = BrandGreen
                            ) {
                                Text(
                                    text = stringResource(id = R.string.kya_banega_view_recipe_btn),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.ssp(),
                                        color = Color.White
                                    ),
                                    modifier = Modifier.padding(horizontal = 10.sdp(), vertical = 5.sdp()),
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            KyaBanegaPillCtaButton(
                text = stringResource(id = R.string.kya_banega_search_another),
                onClick = onResetSearch
            )
        }

        item {
            BannerAdView(remoteConfigManager = remoteConfigManager)
        }
    }
}

/**
 * Recipe Full Detail Screen (with Fixed Symmetrical Layout & Audio Voice Playback)
 */
@Composable
fun RecipeDetailView(
    recipe: RecipeModel,
    isSpeaking: Boolean,
    onToggleSpeech: () -> Unit,
    onShare: () -> Unit,
    remoteConfigManager: RemoteConfigManager
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.sdp(), end = 16.sdp(), top = 4.sdp(), bottom = 80.sdp()),
        verticalArrangement = Arrangement.spacedBy(12.sdp())
    ) {
        // Recipe Hero Card with 3-Column Symmetrical Stats (No text-wrapping bug!)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.sdp()),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.sdp())
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.sdp())
                        .background(LightGreenBg, RoundedCornerShape(8.sdp()))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.sdp()),
                        verticalArrangement = Arrangement.spacedBy(10.sdp())
                    ) {
                        Text(
                            text = recipe.name,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.ssp(),
                                color = TextMain
                            )
                        )

                        if (recipe.subtitle.isNotBlank()) {
                            Text(
                                text = recipe.subtitle,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 12.ssp(),
                                    color = TextSecondary
                                )
                            )
                        }

                        HorizontalDivider(thickness = 1.sdp(), color = Color(0xFFDDE7E3))

                        // 3 Clean, Modern Equal-Width Stat Boxes
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.sdp())
                        ) {
                            RecipeStatBox(
                                label = stringResource(id = R.string.kya_banega_stat_time),
                                value = recipe.cookingTime,
                                icon = R.drawable.ic_timer_clock,
                                iconBg = Color(0xFFE6F9F0),
                                iconTint = BrandGreen,
                                modifier = Modifier.weight(1f)
                            )
                            RecipeStatBox(
                                label = stringResource(id = R.string.kya_banega_stat_servings),
                                value = recipe.servings,
                                icon = R.drawable.ic_servings_people,
                                iconBg = Color(0xFFEFF6FF),
                                iconTint = Color(0xFF2563EB),
                                modifier = Modifier.weight(1f)
                            )
                            RecipeStatBox(
                                label = stringResource(id = R.string.kya_banega_stat_difficulty),
                                value = recipe.difficulty,
                                icon = R.drawable.ic_chef_hat,
                                iconBg = Color(0xFFFFF3E0),
                                iconTint = Color(0xFFE65100),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Fixed Hands-Free Audio Speaker Card (Modern Play/Pause pill badge, no full-card ripple)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onToggleSpeech() },
                shape = RoundedCornerShape(12.sdp()),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.sdp())
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.sdp())
                        .background(
                            if (isSpeaking) Color(0xFFFFEBEE) else LightGreenBg,
                            RoundedCornerShape(8.sdp())
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.sdp(), vertical = 10.sdp()),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Speaker Icon
                        Surface(
                            modifier = Modifier.size(34.sdp()),
                            shape = CircleShape,
                            color = if (isSpeaking) Color(0xFFFFCDD2) else Color.White
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    painter = painterResource(
                                        id = if (isSpeaking) R.drawable.ic_speaker_stop else R.drawable.ic_speaker_sound
                                    ),
                                    contentDescription = null,
                                    tint = if (isSpeaking) Color(0xFFDC2626) else BrandGreenDark,
                                    modifier = Modifier.size(18.sdp())
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.sdp()))

                        // Title & Subtitle with weight so it never pushes the button off screen
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isSpeaking) stringResource(id = R.string.kya_banega_stop_speaking) else stringResource(id = R.string.kya_banega_speak_steps),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.ssp(),
                                    color = if (isSpeaking) Color(0xFFDC2626) else BrandGreenDark
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = stringResource(id = R.string.kya_banega_listen_sub),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 10.ssp(),
                                    color = TextSecondary
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.width(8.sdp()))

                        // Modern Action Pill Button with Circular Play/Pause Icon Badge
                        Surface(
                            shape = RoundedCornerShape(20.sdp()),
                            color = if (isSpeaking) Color(0xFFDC2626) else BrandGreen,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.sdp()))
                                .clickable { onToggleSpeech() }
                        ) {
                            Row(
                                modifier = Modifier.padding(start = 10.sdp(), end = 6.sdp(), top = 5.sdp(), bottom = 5.sdp()),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.sdp())
                            ) {
                                Text(
                                    text = if (isSpeaking) stringResource(id = R.string.kya_banega_btn_stop) else stringResource(id = R.string.kya_banega_btn_listen),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.ssp(),
                                        color = Color.White
                                    ),
                                    maxLines = 1,
                                    softWrap = false
                                )
                                Box(
                                    modifier = Modifier
                                        .size(20.sdp())
                                        .background(Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isSpeaking) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                        contentDescription = null,
                                        tint = if (isSpeaking) Color(0xFFDC2626) else BrandGreen,
                                        modifier = Modifier.size(14.sdp())
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 1: Ingredients Required (Standard White Card + LightGreenBg Inner Structure)
        if (recipe.ingredientsRequired.isNotEmpty()) {
            item {
                RecipeSectionCard(
                    title = stringResource(id = R.string.kya_banega_ingredients_needed),
                    iconRes = R.drawable.ic_cooking_pot,
                    iconBg = Color(0xFFE0F7EC),
                    iconTint = BrandGreen
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.sdp())) {
                        recipe.ingredientsRequired.forEach { ing ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.sdp()),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White, RoundedCornerShape(8.sdp()))
                                    .border(1.sdp(), Color(0xFFE0EDE7), RoundedCornerShape(8.sdp()))
                                    .padding(horizontal = 10.sdp(), vertical = 8.sdp())
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.sdp())
                                        .background(BrandGreen, CircleShape)
                                )
                                Text(
                                    text = ing,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 12.ssp(),
                                        color = TextMain
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 2: Common Spices & Oil (Standard White Card + LightGreenBg Inner Structure)
        if (recipe.commonPantryItems.isNotEmpty()) {
            item {
                RecipeSectionCard(
                    title = stringResource(id = R.string.kya_banega_common_pantry),
                    iconRes = R.drawable.ic_curry_bowl,
                    iconBg = Color(0xFFFFF3E0),
                    iconTint = Color(0xFFE65100)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(8.sdp()))
                            .border(1.sdp(), Color(0xFFE0EDE7), RoundedCornerShape(8.sdp()))
                            .padding(10.sdp())
                    ) {
                        Text(
                            text = recipe.commonPantryItems.joinToString(", "),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.ssp(),
                                color = TextSecondary,
                                lineHeight = 17.ssp()
                            )
                        )
                    }
                }
            }
        }

        // Section 3: Step-by-Step Cooking Steps (Standard White Card + LightGreenBg Inner Structure)
        if (recipe.steps.isNotEmpty()) {
            item {
                RecipeSectionCard(
                    title = stringResource(id = R.string.kya_banega_cooking_steps),
                    iconRes = R.drawable.ic_shopping_checklist,
                    iconBg = Color(0xFFEFF6FF),
                    iconTint = Color(0xFF2563EB)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.sdp())) {
                        recipe.steps.forEachIndexed { index, step ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White, RoundedCornerShape(8.sdp()))
                                    .border(1.sdp(), Color(0xFFE0EDE7), RoundedCornerShape(8.sdp()))
                                    .padding(10.sdp()),
                                horizontalArrangement = Arrangement.spacedBy(10.sdp()),
                                verticalAlignment = Alignment.Top
                            ) {
                                Surface(
                                    modifier = Modifier.size(24.sdp()),
                                    shape = CircleShape,
                                    color = BrandGreen
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "${index + 1}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.ssp(),
                                                color = Color.White
                                            )
                                        )
                                    }
                                }

                                Text(
                                    text = step.stripStepPrefix(),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 12.ssp(),
                                        color = TextMain,
                                        lineHeight = 18.ssp()
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 4: खास शेफ टिप (Chef Secret Tip)
        if (recipe.chefTip.isNotBlank()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.sdp()),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.sdp())
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.sdp())
                            .background(Color(0xFFFFF9E6), RoundedCornerShape(8.sdp()))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.sdp()),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(10.sdp())
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_lightbulb),
                                contentDescription = null,
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(20.sdp())
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(2.sdp())) {
                                Text(
                                    text = stringResource(id = R.string.kya_banega_chef_tip),
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.ssp(),
                                        color = Color(0xFF92400E)
                                    )
                                )
                                Text(
                                    text = recipe.chefTip,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.ssp(),
                                        color = Color(0xFF78350F)
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // 1-Click WhatsApp Share Button (Same compact pill CTA style as previous screen)
        item {
            KyaBanegaPillCtaButton(
                text = stringResource(id = R.string.kya_banega_share_whatsapp),
                onClick = onShare
            )
        }

        item {
            BannerAdView(remoteConfigManager = remoteConfigManager)
        }
    }
}

/**
 * Shared Card Section wrapper matching Apna Gaon white outer frame + LightGreenBg inner structure.
 */
@Composable
private fun RecipeSectionCard(
    title: String,
    iconRes: Int,
    iconBg: Color,
    iconTint: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.sdp()),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.sdp())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.sdp())
                .background(LightGreenBg, RoundedCornerShape(8.sdp()))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.sdp()),
                verticalArrangement = Arrangement.spacedBy(10.sdp())
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.sdp())
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.sdp())
                            .background(iconBg, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(15.sdp())
                        )
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.ssp(),
                            color = TextMain
                        )
                    )
                }

                content()
            }
        }
    }
}

/**
 * Modern Equal-Width Recipe Stat Box with circular tinted icon badge.
 */
@Composable
private fun RecipeStatBox(
    label: String,
    value: String,
    icon: Int,
    iconBg: Color,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.sdp()),
        color = Color.White,
        border = BorderStroke(1.sdp(), Color(0xFFDDE7E3))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.sdp(), vertical = 5.sdp()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.sdp())
        ) {
            Box(
                modifier = Modifier
                    .size(22.sdp())
                    .background(iconBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier.size(12.sdp()),
                    tint = iconTint
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.ssp(),
                    lineHeight = 13.ssp(),
                    color = TextMain
                ),
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.ssp(),
                    lineHeight = 11.ssp(),
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                ),
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Modern Symmetrical Mood Filter Pill (Never Cut Off)
 */
@Composable
private fun MoodPillButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(36.sdp())
            .clip(RoundedCornerShape(20.sdp()))
            .background(if (isSelected) BrandGreen else Color.White)
            .border(
                1.sdp(),
                if (isSelected) BrandGreen else Color(0xFFD0D7DE),
                RoundedCornerShape(20.sdp())
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 11.ssp(),
                color = if (isSelected) Color.White else Color(0xFF334155)
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Polished Chef Advice / Status Highlight Card matching Apna Gaon card style.
 */
@Composable
private fun ChefNoteHighlightCard(
    title: String,
    message: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.sdp()),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.sdp())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.sdp())
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(Color(0xFFE6FAF1), Color(0xFFF3FCF8))
                    ),
                    shape = RoundedCornerShape(8.sdp())
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.sdp(), vertical = 12.sdp()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.sdp())
            ) {
                Box(
                    modifier = Modifier
                        .size(40.sdp())
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.sdp(), BrandGreen.copy(alpha = 0.25f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_chef_hat),
                        contentDescription = null,
                        tint = BrandGreen,
                        modifier = Modifier.size(21.sdp())
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.sdp())
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.ssp(),
                            color = BrandGreenDark
                        )
                    )
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.ssp(),
                            color = TextMain,
                            lineHeight = 17.ssp()
                        )
                    )
                }
            }
        }
    }
}

/**
 * Shared compact pill CTA button matching the primary action style.
 */
@Composable
internal fun KyaBanegaPillCtaButton(
    text: String,
    onClick: () -> Unit,
    leadingIcon: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth(0.82f)
                .height(44.sdp()),
            shape = RoundedCornerShape(27.sdp()),
            contentPadding = PaddingValues(horizontal = 16.sdp(), vertical = 0.sdp()),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandGreen,
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp,
                focusedElevation = 0.dp,
                hoveredElevation = 0.dp,
                disabledElevation = 0.dp
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.sdp())
            ) {
                if (leadingIcon != null) {
                    leadingIcon()
                }
                Text(
                    text = text,
                    fontSize = 13.ssp(),
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
