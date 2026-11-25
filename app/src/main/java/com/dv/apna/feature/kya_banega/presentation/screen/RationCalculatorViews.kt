package com.dv.apna.feature.kya_banega.presentation.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.Celebration
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dv.apna.R
import com.dv.apna.core.ads.BannerAdView
import com.dv.apna.core.config.RemoteConfigManager
import com.dv.apna.core.utils.sdp
import com.dv.apna.core.utils.ssp
import com.dv.apna.feature.kya_banega.data.datasource.CateringDatabase
import com.dv.apna.feature.kya_banega.domain.engine.CateringCalculatorEngine
import com.dv.apna.feature.kya_banega.domain.model.*
import com.dv.apna.feature.kya_banega.presentation.event.KyaBanegaEvent
import com.dv.apna.feature.kya_banega.presentation.state.KitchenHubTab
import com.dv.apna.feature.kya_banega.presentation.state.RationCalculatorState
import com.dv.apna.feature.kya_banega.presentation.state.RationStep
import com.dv.apna.feature.kya_banega.presentation.state.RationValidationField
import com.dv.apna.feature.kya_banega.presentation.util.RationShareHelper
import kotlin.math.roundToInt

// Matching exact button color from CommonComponents.kt & Crop Buyers
private val BrandGreen = Color(0xFF38C792)
private val BrandGreenDark = Color(0xFF1B6A4C)
private val LightGreenBg = Color(0xFFEFFAF6)
private val LightGreenBgSelected = Color(0xFFE2F7EF)
private val CardBorderColor = Color(0xFFD9D9D9)
private val TextMain = Color(0xFF1E232C)
private val TextSecondary = Color(0xFF64748B)
private val ErrorRed = Color(0xFFDC2626)

// ==================== TOP KITCHEN HUB TAB BAR ====================
@Composable
fun KitchenHubTabBar(
    selectedTab: KitchenHubTab,
    onTabSelected: (KitchenHubTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.sdp(), vertical = 6.sdp()),
        shape = RoundedCornerShape(27.sdp()),
        color = Color(0xFFE2F4EE)
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(3.sdp())
        ) {
            val tabWidth = maxWidth / 2
            val indicatorOffset by animateDpAsState(
                targetValue = if (selectedTab == KitchenHubTab.RECIPES) 0.dp else tabWidth,
                animationSpec = spring(
                    dampingRatio = 0.85f,
                    stiffness = Spring.StiffnessMediumLow
                ),
                label = "IndicatorOffset"
            )

            // Animated Sliding Pill Indicator
            Box(
                modifier = Modifier
                    .offset(x = indicatorOffset)
                    .width(tabWidth)
                    .height(38.sdp())
                    .clip(RoundedCornerShape(24.sdp()))
                    .background(BrandGreen)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.sdp()),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tab 1: What to Cook?
                val isRecipeSelected = selectedTab == KitchenHubTab.RECIPES
                val textColor1 by animateColorAsState(
                    targetValue = if (isRecipeSelected) Color.White else BrandGreenDark,
                    animationSpec = tween(200),
                    label = "TextColor1"
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(24.sdp()))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onTabSelected(KitchenHubTab.RECIPES) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.kitchen_hub_tab_recipes),
                        fontSize = 12.ssp(),
                        fontWeight = if (isRecipeSelected) FontWeight.Bold else FontWeight.Medium,
                        color = textColor1,
                        maxLines = 1
                    )
                }

                // Tab 2: Ration Calculator
                val isRationSelected = selectedTab == KitchenHubTab.RATION_CALCULATOR
                val textColor2 by animateColorAsState(
                    targetValue = if (isRationSelected) Color.White else BrandGreenDark,
                    animationSpec = tween(200),
                    label = "TextColor2"
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(24.sdp()))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onTabSelected(KitchenHubTab.RATION_CALCULATOR) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.kitchen_hub_tab_ration),
                        fontSize = 12.ssp(),
                        fontWeight = if (isRationSelected) FontWeight.Bold else FontWeight.Medium,
                        color = textColor2,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

// ==================== 5-STEP INDICATOR ====================
@Composable
fun RationStepIndicator(
    currentStep: RationStep,
    modifier: Modifier = Modifier
) {
    val steps = RationStep.entries.toTypedArray()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.sdp(), vertical = 8.sdp()),
        verticalAlignment = Alignment.Top
    ) {
        steps.forEachIndexed { index, step ->
            val isCurrent = step == currentStep
            val isPast = step.stepNumber < currentStep.stepNumber

            val stepLabel = when (step) {
                RationStep.OCCASION -> stringResource(id = R.string.ration_step1_name)
                RationStep.GUESTS -> stringResource(id = R.string.ration_step2_name)
                RationStep.MENU -> stringResource(id = R.string.ration_step3_name)
                RationStep.QUANTITY -> stringResource(id = R.string.ration_step4_name)
                RationStep.SHOPPING_LIST -> stringResource(id = R.string.ration_step5_name)
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                // Fixed-height Box keeps horizontal connector bars centered on the larger step circles
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.sdp()),
                    contentAlignment = Alignment.Center
                ) {
                    if (index > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .fillMaxWidth(0.5f)
                                .padding(end = 20.sdp())
                                .height(2.sdp())
                                .background(
                                    if (step.stepNumber <= currentStep.stepNumber) BrandGreen
                                    else Color(0xFFE2E8F0)
                                )
                        )
                    }

                    if (index < steps.size - 1) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .fillMaxWidth(0.5f)
                                .padding(start = 20.sdp())
                                .height(2.sdp())
                                .background(
                                    if (step.stepNumber < currentStep.stepNumber) BrandGreen
                                    else Color(0xFFE2E8F0)
                                )
                        )
                    }

                    // Halo ring separates larger circle node cleanly from shorter connector lines
                    Box(
                        modifier = Modifier
                            .size(if (isCurrent) 36.sdp() else 32.sdp())
                            .clip(CircleShape)
                            .background(if (isCurrent) Color(0xFFD5F5E7) else Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.sdp())
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isCurrent -> BrandGreen
                                        isPast -> Color(0xFFE5F9F0)
                                        else -> Color.White
                                    }
                                )
                                .then(
                                    if (!isCurrent) {
                                        Modifier.border(
                                            width = 1.sdp(),
                                            color = if (isPast) BrandGreen else Color(0xFFCBD5E1),
                                            shape = CircleShape
                                        )
                                    } else {
                                        Modifier
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isPast) {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = null,
                                    tint = BrandGreenDark,
                                    modifier = Modifier.size(15.sdp())
                                )
                            } else {
                                Text(
                                    text = "${step.stepNumber}",
                                    fontSize = 13.ssp(),
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCurrent) Color.White else TextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.sdp()))

                Text(
                    text = stepLabel,
                    fontSize = 9.ssp(),
                    lineHeight = 12.ssp(),
                    fontWeight = when {
                        isCurrent -> FontWeight.Bold
                        isPast -> FontWeight.SemiBold
                        else -> FontWeight.Medium
                    },
                    color = when {
                        isCurrent -> BrandGreenDark
                        isPast -> BrandGreen
                        else -> TextSecondary
                    },
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ==================== STEP 1: OCCASION SELECTION ====================
@Composable
fun RationOccasionStepView(
    selectedOccasion: OccasionType?,
    validationErrorField: RationValidationField? = null,
    onSelectOccasion: (OccasionType) -> Unit,
    onNext: () -> Unit
) {
    val context = LocalContext.current
    val isHindi = context.resources.configuration.locales[0].language.equals("hi", ignoreCase = true)

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(start = 16.sdp(), end = 16.sdp(), top = 4.sdp(), bottom = 14.sdp()),
            verticalArrangement = Arrangement.spacedBy(10.sdp())
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 2.sdp()),
                    verticalArrangement = Arrangement.spacedBy(3.sdp())
                ) {
                    Text(
                        text = stringResource(id = R.string.ration_step1_header),
                        fontSize = 15.ssp(),
                        lineHeight = 20.ssp(),
                        fontWeight = FontWeight.Bold,
                        color = TextMain
                    )
                    Text(
                        text = stringResource(id = R.string.ration_step1_sub),
                        fontSize = 11.ssp(),
                        lineHeight = 16.ssp(),
                        color = TextSecondary
                    )
                }
            }

            items(OccasionType.entries.toTypedArray(), key = { it.name }) { occasion ->
                RationOccasionCard(
                    occasion = occasion,
                    isSelected = occasion == selectedOccasion,
                    isHindi = isHindi,
                    onClick = { onSelectOccasion(occasion) }
                )
            }
        }

        if (validationErrorField == RationValidationField.OCCASION) {
            Text(
                text = stringResource(id = R.string.ration_err_select_occasion),
                fontSize = 12.ssp(),
                fontWeight = FontWeight.Medium,
                color = ErrorRed,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.sdp(), vertical = 2.sdp())
            )
        }

        // Fixed Bottom Primary CTA Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.sdp(), vertical = 10.sdp()),
            contentAlignment = Alignment.Center
        ) {
            KyaBanegaPillCtaButton(
                text = stringResource(id = R.string.ration_btn_select_guests),
                onClick = onNext
            )
        }
    }
}

@Composable
private fun RationOccasionCard(
    occasion: OccasionType,
    isSelected: Boolean,
    isHindi: Boolean,
    onClick: () -> Unit
) {
    val (iconBg, iconTint) = when (occasion) {
        OccasionType.HOME -> Color(0xFFE0F7EC) to BrandGreen
        OccasionType.PARTY -> Color(0xFFEFF6FF) to Color(0xFF2563EB)
        OccasionType.BHANDARA -> Color(0xFFFFF3E0) to Color(0xFFE65100)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() },
        shape = RoundedCornerShape(12.sdp()),
        color = Color.White,
        border = if (isSelected) BorderStroke(width = 1.5.dp, color = BrandGreen) else null,
        shadowElevation = if (isSelected) 0.dp else 2.sdp()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.sdp())
                .background(
                    if (isSelected) Color(0xFFE4F8F0) else LightGreenBg,
                    shape = RoundedCornerShape(8.sdp())
                )
                .padding(horizontal = 12.sdp(), vertical = 9.sdp())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.sdp())
                        .clip(CircleShape)
                        .background(if (isSelected) Color.White else iconBg)
                        .then(
                            if (isSelected) {
                                Modifier.border(1.sdp(), BrandGreen.copy(alpha = 0.35f), CircleShape)
                            } else {
                                Modifier
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (occasion == OccasionType.PARTY) {
                        Icon(
                            imageVector = Icons.Rounded.Celebration,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(18.sdp())
                        )
                    } else {
                        Icon(
                            painter = painterResource(
                                id = if (occasion == OccasionType.HOME) R.drawable.ic_cooking_pot
                                else R.drawable.ic_catering
                            ),
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(18.sdp())
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.sdp()))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(1.sdp())
                ) {
                    Text(
                        text = occasion.title(isHindi),
                        fontSize = 13.ssp(),
                        lineHeight = 17.ssp(),
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) BrandGreenDark else TextMain
                    )
                    Text(
                        text = occasion.subtitle(isHindi),
                        fontSize = 11.ssp(),
                        lineHeight = 14.ssp(),
                        color = TextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(10.sdp()))

                Box(
                    modifier = Modifier
                        .size(22.sdp())
                        .clip(CircleShape)
                        .background(if (isSelected) BrandGreen else Color.White)
                        .border(
                            width = 1.sdp(),
                            color = if (isSelected) BrandGreen else Color(0xFFCBD5E1),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(13.sdp())
                        )
                    }
                }
            }
        }
    }
}

// ==================== STEP 2: GUEST DETAILS & APPETITE ====================
@Composable
fun RationGuestsStepView(
    adultsInputText: String,
    kidsInputText: String,
    selectedAppetite: AppetiteLevel?,
    validationErrorField: RationValidationField? = null,
    onAdultsInputChange: (String) -> Unit,
    onKidsInputChange: (String) -> Unit,
    onSelectAppetite: (AppetiteLevel) -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    val adultsVal = adultsInputText.toIntOrNull()
    val kidsVal = kidsInputText.toIntOrNull()
    val displayAdults = adultsVal ?: 0
    val displayKids = kidsVal ?: 0
    val effectivePlates = if (displayAdults <= 0 && displayKids <= 0) {
        0
    } else {
        ((displayAdults + (displayKids * 0.5f)) * (selectedAppetite?.multiplier ?: 1f)).roundToInt()
    }

    val adultsError = if (validationErrorField == RationValidationField.ADULTS) {
        stringResource(id = R.string.ration_err_adults_required)
    } else {
        null
    }
    val kidsError = if (validationErrorField == RationValidationField.CHILDREN) {
        stringResource(id = R.string.ration_err_kids_required)
    } else {
        null
    }
    val appetiteError = if (validationErrorField == RationValidationField.APPETITE) {
        stringResource(id = R.string.ration_err_appetite_required)
    } else {
        null
    }

    val listState = rememberLazyListState()
    LaunchedEffect(validationErrorField) {
        when (validationErrorField) {
            RationValidationField.ADULTS -> listState.animateScrollToItem(1)
            RationValidationField.CHILDREN -> listState.animateScrollToItem(2)
            RationValidationField.APPETITE -> listState.animateScrollToItem(3)
            else -> Unit
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(start = 16.sdp(), end = 16.sdp(), top = 6.sdp(), bottom = 14.sdp()),
            verticalArrangement = Arrangement.spacedBy(12.sdp())
        ) {
        // Effective Plate Summary Card (2-Row Layout: Zero text clipping!)
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
                        .background(LightGreenBg, shape = RoundedCornerShape(8.sdp()))
                        .padding(12.sdp())
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.sdp())
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.sdp())
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.sdp())
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .border(1.dp, BrandGreen.copy(alpha = 0.25f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_servings_people),
                                    contentDescription = null,
                                    tint = BrandGreen,
                                    modifier = Modifier.size(18.sdp())
                                )
                            }

                            Text(
                                text = stringResource(id = R.string.ration_effective_plates_title),
                                fontSize = 12.ssp(),
                                lineHeight = 16.ssp(),
                                fontWeight = FontWeight.Medium,
                                color = TextSecondary,
                                modifier = Modifier.weight(1f)
                            )

                            Text(
                                text = stringResource(
                                    id = R.string.ration_effective_plates_unit,
                                    effectivePlates
                                ),
                                fontSize = 16.ssp(),
                                fontWeight = FontWeight.Bold,
                                color = BrandGreenDark,
                                maxLines = 1,
                                softWrap = false
                            )
                        }

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.sdp()),
                            color = Color.White,
                            border = BorderStroke(1.dp, Color(0xFFDDE7E3))
                        ) {
                            Text(
                                text = stringResource(
                                    id = R.string.ration_guest_summary_badge,
                                    displayAdults,
                                    displayKids
                                ),
                                fontSize = 12.ssp(),
                                fontWeight = FontWeight.SemiBold,
                                color = BrandGreenDark,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.sdp(), vertical = 7.sdp())
                            )
                        }
                    }
                }
            }
        }

        // Custom Input 1: Adults Count
        item {
            GuestCountCard(
                title = stringResource(id = R.string.ration_adults_title),
                subtitle = stringResource(id = R.string.ration_adults_sub),
                placeholder = stringResource(id = R.string.ration_adults_hint),
                inputText = adultsInputText,
                errorMessage = adultsError,
                iconRes = R.drawable.ic_users_group,
                iconBg = Color(0xFFE0F7EC),
                iconTint = BrandGreen,
                onInputChange = onAdultsInputChange
            )
        }

        // Custom Input 2: Children Count
        item {
            GuestCountCard(
                title = stringResource(id = R.string.ration_kids_title),
                subtitle = stringResource(id = R.string.ration_kids_sub),
                placeholder = stringResource(id = R.string.ration_kids_hint),
                inputText = kidsInputText,
                errorMessage = kidsError,
                iconRes = R.drawable.ic_servings_people,
                iconBg = Color(0xFFEFF6FF),
                iconTint = Color(0xFF2563EB),
                onInputChange = onKidsInputChange
            )
        }

        // Appetite Selector (Standard White Card + LightGreenBg Structure)
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
                        .background(LightGreenBg, shape = RoundedCornerShape(8.sdp()))
                        .padding(12.sdp())
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.sdp())) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.sdp())
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.sdp())
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFF3E0)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_curry_bowl),
                                    contentDescription = null,
                                    tint = Color(0xFFE65100),
                                    modifier = Modifier.size(17.sdp())
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(id = R.string.ration_appetite_title),
                                    fontSize = 13.ssp(),
                                    fontWeight = FontWeight.Bold,
                                    color = TextMain
                                )
                                Text(
                                    text = stringResource(id = R.string.ration_appetite_desc),
                                    fontSize = 11.ssp(),
                                    lineHeight = 15.ssp(),
                                    color = TextSecondary
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.sdp())
                        ) {
                            AppetiteLevel.entries.forEach { appLevel ->
                                val isSelected = selectedAppetite == appLevel
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) { onSelectAppetite(appLevel) },
                                    shape = RoundedCornerShape(10.sdp()),
                                    color = if (isSelected) BrandGreen else Color.White,
                                    border = BorderStroke(
                                        1.dp,
                                        when {
                                            isSelected -> BrandGreen
                                            appetiteError != null -> ErrorRed
                                            else -> Color(0xFFDDE7E3)
                                        }
                                    )
                                ) {
                                    Box(
                                        modifier = Modifier.padding(vertical = 10.sdp(), horizontal = 4.sdp()),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = when (appLevel) {
                                                AppetiteLevel.LIGHT -> stringResource(id = R.string.ration_appetite_light)
                                                AppetiteLevel.NORMAL -> stringResource(id = R.string.ration_appetite_normal)
                                                AppetiteLevel.HEAVY -> stringResource(id = R.string.ration_appetite_heavy)
                                            },
                                            fontSize = 11.ssp(),
                                            lineHeight = 14.ssp(),
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else TextMain,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }

                        if (appetiteError != null) {
                            Text(
                                text = appetiteError,
                                fontSize = 11.ssp(),
                                fontWeight = FontWeight.Medium,
                                color = ErrorRed
                            )
                        }
                    }
                }
            }
        }

        }

        // Fixed Bottom Primary Action Button + Simple Back Text
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.sdp(), bottom = 8.sdp()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.sdp())
        ) {
            KyaBanegaPillCtaButton(
                text = stringResource(id = R.string.ration_btn_select_menu),
                onClick = onNext
            )
            RationStepBackText(onClick = onPrevious)
        }
    }
}

@Composable
private fun GuestCountCard(
    title: String,
    subtitle: String,
    placeholder: String,
    inputText: String,
    errorMessage: String? = null,
    iconRes: Int,
    iconBg: Color,
    iconTint: Color,
    onInputChange: (String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    var isFocused by remember { mutableStateOf(false) }

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
                .background(LightGreenBg, shape = RoundedCornerShape(8.sdp()))
                .padding(12.sdp())
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.sdp())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.sdp())
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.sdp())
                            .clip(CircleShape)
                            .background(iconBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(16.sdp())
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(1.sdp())
                    ) {
                        Text(
                            text = title,
                            fontSize = 13.ssp(),
                            lineHeight = 17.ssp(),
                            fontWeight = FontWeight.Bold,
                            color = TextMain,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = subtitle,
                            fontSize = 11.ssp(),
                            lineHeight = 14.ssp(),
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Single EditText matching AddBusinessScreen FormField style
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.sdp())
                        .clip(RoundedCornerShape(10.sdp()))
                        .background(Color.White)
                        .border(
                            width = 1.dp,
                            color = when {
                                isFocused -> BrandGreen
                                errorMessage != null -> ErrorRed
                                else -> CardBorderColor
                            },
                            shape = RoundedCornerShape(10.sdp())
                        )
                        .padding(horizontal = 12.sdp()),
                    contentAlignment = Alignment.CenterStart
                ) {
                    BasicTextField(
                        value = inputText,
                        onValueChange = onInputChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { state ->
                                isFocused = state.isFocused
                            },
                        textStyle = TextStyle(
                            color = TextMain,
                            fontSize = 13.ssp(),
                            fontWeight = FontWeight.Normal
                        ),
                        singleLine = true,
                        cursorBrush = SolidColor(BrandGreen),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            }
                        ),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (inputText.isEmpty()) {
                                    Text(
                                        text = placeholder,
                                        color = Color(0xFF8391A1),
                                        fontSize = 12.ssp(),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }

                if (!errorMessage.isNullOrBlank()) {
                    Text(
                        text = errorMessage,
                        fontSize = 11.ssp(),
                        fontWeight = FontWeight.Medium,
                        color = ErrorRed
                    )
                }
            }
        }
    }
}

// ==================== STEP 3: MENU BUILDER ====================
@Composable
fun RationMenuBuilderStepView(
    selectedCategory: DishCategory,
    selectedDishIds: Set<String>,
    validationErrorField: RationValidationField? = null,
    onSelectCategory: (DishCategory) -> Unit,
    onToggleDish: (String) -> Unit,
    onPrevious: () -> Unit,
    onCalculate: () -> Unit
) {
    val context = LocalContext.current
    val isHindi = context.resources.configuration.locales[0].language.equals("hi", ignoreCase = true)
    val dishesInCat = remember(selectedCategory) {
        CateringDatabase.allDishes.filter { it.category == selectedCategory }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Category Filter Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.sdp(), vertical = 4.sdp()),
            horizontalArrangement = Arrangement.spacedBy(8.sdp())
        ) {
            items(DishCategory.entries.toTypedArray(), key = { it.name }) { category ->
                val isSelected = category == selectedCategory
                Surface(
                    shape = RoundedCornerShape(18.sdp()),
                    color = if (isSelected) BrandGreen else Color.White,
                    border = if (isSelected) null else BorderStroke(1.dp, Color(0xFFDDE7E3)),
                    shadowElevation = 0.dp,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onSelectCategory(category) }
                ) {
                    Text(
                        text = category.title(isHindi),
                        fontSize = 11.ssp(),
                        lineHeight = 15.ssp(),
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else TextMain,
                        maxLines = 1,
                        softWrap = false,
                        modifier = Modifier.padding(horizontal = 13.sdp(), vertical = 6.sdp())
                    )
                }
            }
        }

        // Dishes List for Selected Category
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(start = 16.sdp(), end = 16.sdp(), top = 4.sdp(), bottom = 12.sdp()),
            verticalArrangement = Arrangement.spacedBy(10.sdp())
        ) {
            items(dishesInCat, key = { it.id }) { dish ->
                RationDishCard(
                    dish = dish,
                    isChecked = selectedDishIds.contains(dish.id),
                    isHindi = isHindi,
                    onClick = { onToggleDish(dish.id) }
                )
            }
        }

        if (validationErrorField == RationValidationField.MENU) {
            Text(
                text = stringResource(id = R.string.ration_err_select_dish),
                fontSize = 11.ssp(),
                fontWeight = FontWeight.Medium,
                color = ErrorRed,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.sdp(), vertical = 2.sdp())
            )
        }

        // Primary CTA + Simple Back Text
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.sdp(), bottom = 8.sdp()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.sdp())
        ) {
            KyaBanegaPillCtaButton(
                text = stringResource(id = R.string.ration_btn_calc_dishes, selectedDishIds.size),
                onClick = onCalculate
            )
            RationStepBackText(onClick = onPrevious)
        }
    }
}

@Composable
private fun RationDishCard(
    dish: CateringDish,
    isChecked: Boolean,
    isHindi: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() },
        shape = RoundedCornerShape(12.sdp()),
        color = Color.White,
        border = if (isChecked) BorderStroke(width = 1.5.dp, color = BrandGreen) else null,
        shadowElevation = if (isChecked) 0.dp else 2.sdp()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.sdp())
                .background(
                    if (isChecked) Color(0xFFE4F8F0) else LightGreenBg,
                    shape = RoundedCornerShape(8.sdp())
                )
                .padding(horizontal = 12.sdp(), vertical = 10.sdp())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.sdp())
                ) {
                    Text(
                        text = dish.displayName(isHindi),
                        fontSize = 13.ssp(),
                        lineHeight = 17.ssp(),
                        fontWeight = FontWeight.Bold,
                        color = if (isChecked) BrandGreenDark else TextMain,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = dish.displayDescription(isHindi),
                        fontSize = 11.ssp(),
                        lineHeight = 15.ssp(),
                        color = TextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.sdp()))
                    Surface(
                        shape = RoundedCornerShape(6.sdp()),
                        color = Color.White,
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (isChecked) BrandGreen.copy(alpha = 0.25f) else Color(0xFFDDE7E3)
                        ),
                        shadowElevation = 0.dp
                    ) {
                        Text(
                            text = stringResource(
                                id = R.string.ration_portion_standard,
                                dish.cookedServingPerPerson.toInt(),
                                dish.displayUnit(isHindi)
                            ),
                            fontSize = 10.ssp(),
                            fontWeight = FontWeight.SemiBold,
                            color = BrandGreenDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(horizontal = 8.sdp(), vertical = 3.sdp())
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.sdp()))

                Box(
                    modifier = Modifier
                        .size(22.sdp())
                        .clip(CircleShape)
                        .background(if (isChecked) BrandGreen else Color.White)
                        .border(
                            width = 1.dp,
                            color = if (isChecked) BrandGreen else Color(0xFFCBD5E1),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isChecked) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(13.sdp())
                        )
                    }
                }
            }
        }
    }
}

// ==================== STEP 4: QUANTITY BREAKDOWN ====================
@Composable
fun RationQuantityStepView(
    result: CateringCalculationResult,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    remoteConfigManager: RemoteConfigManager
) {
    val context = LocalContext.current
    val isHindi = context.resources.configuration.locales[0].language.equals("hi", ignoreCase = true)

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(start = 16.sdp(), end = 16.sdp(), top = 6.sdp(), bottom = 14.sdp()),
            verticalArrangement = Arrangement.spacedBy(12.sdp())
        ) {
        // Compact Header Summary Card (matching Step 2 Summary Card)
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.sdp()),
                color = Color.White,
                shadowElevation = 2.sdp()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.sdp())
                        .background(LightGreenBg, shape = RoundedCornerShape(8.sdp()))
                        .padding(12.sdp())
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.sdp())
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.sdp())
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(1.dp, BrandGreen.copy(alpha = 0.25f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_cooking_pot),
                                contentDescription = null,
                                tint = BrandGreen,
                                modifier = Modifier.size(17.sdp())
                            )
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(2.sdp())
                        ) {
                            Text(
                                text = stringResource(id = R.string.ration_cooked_header),
                                fontSize = 13.ssp(),
                                lineHeight = 17.ssp(),
                                fontWeight = FontWeight.Bold,
                                color = TextMain
                            )
                            Text(
                                text = stringResource(id = R.string.ration_cooked_sub),
                                fontSize = 11.ssp(),
                                lineHeight = 14.ssp(),
                                color = TextSecondary
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.sdp()),
                            color = BrandGreen,
                            shadowElevation = 0.dp
                        ) {
                            Text(
                                text = stringResource(
                                    id = R.string.ration_effective_plates_unit,
                                    result.guestConfig.effectiveGuests.roundToInt()
                                ),
                                fontSize = 11.ssp(),
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                softWrap = false,
                                modifier = Modifier.padding(horizontal = 10.sdp(), vertical = 5.sdp())
                            )
                        }
                    }
                }
            }
        }

        // Dish-by-dish Breakdown
        items(result.dishRequirements, key = { it.dish.id }) { req ->
            RationDishQuantityCard(req = req, isHindi = isHindi)
        }

        // Ad Banner
        item {
            BannerAdView(
                remoteConfigManager = remoteConfigManager,
                modifier = Modifier.fillMaxWidth()
            )
        }

        }

        // Fixed Bottom Primary CTA + Simple Back Text
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.sdp(), bottom = 8.sdp()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.sdp())
        ) {
            KyaBanegaPillCtaButton(
                text = stringResource(id = R.string.ration_btn_view_list),
                onClick = onNext
            )
            RationStepBackText(onClick = onPrevious)
        }
    }
}

@Composable
private fun RationStepBackText(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = stringResource(id = R.string.back)
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 13.ssp(),
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(16.sdp()))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onClick() }
                .padding(horizontal = 24.sdp(), vertical = 8.sdp())
        )
    }
}

@Composable
private fun RationDishQuantityCard(
    req: CalculatedDishRequirement,
    isHindi: Boolean
) {
    val formattedCooked = CateringCalculatorEngine.formatCookedQuantity(
        amount = req.totalCookedAmount,
        unit = req.dish.servingUnit,
        isHindi = isHindi,
        displayUnit = req.dish.displayUnit(isHindi)
    )
    val chefTip = req.dish.displayChefTip(isHindi)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.sdp()),
        color = Color.White,
        shadowElevation = 2.sdp()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.sdp())
                .background(LightGreenBg, shape = RoundedCornerShape(8.sdp()))
                .padding(12.sdp())
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.sdp())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.sdp())
                    ) {
                        Text(
                            text = req.dish.displayName(isHindi),
                            fontSize = 14.ssp(),
                            lineHeight = 18.ssp(),
                            fontWeight = FontWeight.Bold,
                            color = TextMain,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = req.dish.category.title(isHindi),
                            fontSize = 11.ssp(),
                            lineHeight = 14.ssp(),
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.width(8.sdp()))

                    Surface(
                        shape = RoundedCornerShape(8.sdp()),
                        color = Color.White,
                        border = BorderStroke(1.dp, BrandGreen.copy(alpha = 0.35f)),
                        shadowElevation = 0.dp
                    ) {
                        Text(
                            text = formattedCooked,
                            fontSize = 11.ssp(),
                            fontWeight = FontWeight.Bold,
                            color = BrandGreenDark,
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier.padding(horizontal = 10.sdp(), vertical = 5.sdp())
                        )
                    }
                }

                // Clean white raw ingredients box inside green card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.sdp()),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFDDE7E3)),
                    shadowElevation = 0.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.sdp(), vertical = 8.sdp()),
                        verticalArrangement = Arrangement.spacedBy(5.sdp())
                    ) {
                        Text(
                            text = stringResource(id = R.string.ration_raw_breakdown_label),
                            fontSize = 11.ssp(),
                            fontWeight = FontWeight.SemiBold,
                            color = BrandGreenDark
                        )
                        HorizontalDivider(thickness = 1.dp, color = Color(0xFFEEF5F2))

                        req.rawBreakdown.forEach { ing ->
                            val qtyText = CateringCalculatorEngine.formatRawAmount(
                                amount = ing.totalAmount,
                                unit = ing.unit,
                                isHindi = isHindi
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "• ${ing.displayName(isHindi)}",
                                    fontSize = 12.ssp(),
                                    lineHeight = 16.ssp(),
                                    color = TextMain,
                                    modifier = Modifier.weight(1f),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.width(8.sdp()))
                                Text(
                                    text = qtyText,
                                    fontSize = 12.ssp(),
                                    fontWeight = FontWeight.Bold,
                                    color = BrandGreenDark,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                    }
                }

                if (chefTip.isNotBlank()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.sdp()),
                        color = Color(0xFFFFF8E7),
                        border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                        shadowElevation = 0.dp
                    ) {
                        Text(
                            text = stringResource(id = R.string.ration_chef_tip_label, chefTip),
                            fontSize = 11.ssp(),
                            lineHeight = 15.ssp(),
                            color = Color(0xFF92400E),
                            modifier = Modifier.padding(horizontal = 10.sdp(), vertical = 7.sdp())
                        )
                    }
                }
            }
        }
    }
}

// ==================== STEP 5: MASTER SHOPPING LIST & BUDGET ====================
@Composable
fun RationShoppingListStepView(
    result: CateringCalculationResult,
    checkedItems: Set<String>,
    onToggleItem: (String) -> Unit,
    onReset: () -> Unit,
    onShareWhatsApp: () -> Unit,
    remoteConfigManager: RemoteConfigManager
) {
    val context = LocalContext.current
    val isHindi = context.resources.configuration.locales[0].language.equals("hi", ignoreCase = true)

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(start = 16.sdp(), end = 16.sdp(), top = 6.sdp(), bottom = 14.sdp()),
            verticalArrangement = Arrangement.spacedBy(12.sdp())
        ) {
        // Hero Budget Card matching Steps 1-4 Card Design
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.sdp()),
                color = Color.White,
                shadowElevation = 2.sdp()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.sdp())
                        .background(LightGreenBg, shape = RoundedCornerShape(8.sdp()))
                        .padding(12.sdp())
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.sdp())
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.sdp())
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.sdp())
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .border(1.dp, BrandGreen.copy(alpha = 0.25f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.wallet),
                                    contentDescription = null,
                                    tint = BrandGreen,
                                    modifier = Modifier.size(18.sdp())
                                )
                            }

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(2.sdp())
                            ) {
                                Text(
                                    text = stringResource(id = R.string.ration_budget_title),
                                    fontSize = 12.ssp(),
                                    lineHeight = 16.ssp(),
                                    color = TextSecondary
                                )
                                Text(
                                    text = stringResource(
                                        id = R.string.ration_budget_range,
                                        result.estimatedCostMin,
                                        result.estimatedCostMax
                                    ),
                                    fontSize = 20.ssp(),
                                    lineHeight = 25.ssp(),
                                    fontWeight = FontWeight.Bold,
                                    color = BrandGreenDark
                                )
                            }
                        }

                        // Clean white inner strip for Per-Plate Cost (prevents wrapping collision)
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.sdp()),
                            color = Color.White,
                            border = BorderStroke(1.dp, Color(0xFFDDE7E3)),
                            shadowElevation = 0.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.sdp(), vertical = 8.sdp()),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(id = R.string.ration_plate_cost_label),
                                    fontSize = 11.ssp(),
                                    color = TextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.sdp()))
                                Text(
                                    text = stringResource(
                                        id = R.string.ration_plate_cost_value,
                                        result.costPerPlateMin,
                                        result.costPerPlateMax
                                    ),
                                    fontSize = 12.ssp(),
                                    fontWeight = FontWeight.Bold,
                                    color = BrandGreenDark,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                    }
                }
            }
        }

        // Compact Home Availability Tip Note
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE5F9F0), shape = RoundedCornerShape(8.sdp()))
                    .border(1.dp, BrandGreen.copy(alpha = 0.25f), RoundedCornerShape(8.sdp()))
                    .padding(horizontal = 10.sdp(), vertical = 8.sdp()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.sdp())
            ) {
                Box(
                    modifier = Modifier
                        .size(18.sdp())
                        .clip(RoundedCornerShape(4.sdp()))
                        .background(BrandGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.sdp())
                    )
                }
                Text(
                    text = stringResource(id = R.string.ration_home_tick_hint),
                    fontSize = 11.ssp(),
                    lineHeight = 15.ssp(),
                    fontWeight = FontWeight.Medium,
                    color = BrandGreenDark,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Categorized Kirana / Mandi / Dairy Checklist Cards
        items(result.groceryGroups, key = { it.category.name }) { group ->
            RationGroceryCategoryCard(
                group = group,
                checkedItems = checkedItems,
                isHindi = isHindi,
                onToggleItem = onToggleItem
            )
        }

        // Ad Banner
        item {
            BannerAdView(
                remoteConfigManager = remoteConfigManager,
                modifier = Modifier.fillMaxWidth()
            )
        }

        }

        // Fixed Bottom Actions: Share List on WhatsApp + Text-Only Create New Menu / Recalculate
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.sdp(), bottom = 8.sdp()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.sdp())
        ) {
            Button(
                onClick = onShareWhatsApp,
                modifier = Modifier
                    .fillMaxWidth(0.86f)
                    .height(44.sdp()),
                shape = RoundedCornerShape(27.sdp()),
                contentPadding = PaddingValues(horizontal = 16.sdp(), vertical = 0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
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
                    Image(
                        painter = painterResource(id = R.drawable.iv_whatsapp),
                        contentDescription = null,
                        modifier = Modifier.size(18.sdp())
                    )
                    Text(
                        text = stringResource(id = R.string.ration_share_whatsapp),
                        fontSize = 13.ssp(),
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            RationStepBackText(
                onClick = onReset,
                text = stringResource(id = R.string.ration_btn_recalculate)
            )
        }
    }
}

@Composable
private fun RationGroceryCategoryCard(
    group: GroceryCategoryGroup,
    checkedItems: Set<String>,
    isHindi: Boolean,
    onToggleItem: (String) -> Unit
) {
    val categoryColor = Color(group.category.colorHex)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.sdp()),
        color = Color.White,
        shadowElevation = 2.sdp()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.sdp())
                .background(LightGreenBg, shape = RoundedCornerShape(8.sdp()))
                .padding(12.sdp())
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.sdp())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = group.category.title(isHindi),
                        fontSize = 13.ssp(),
                        lineHeight = 17.ssp(),
                        fontWeight = FontWeight.Bold,
                        color = categoryColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.sdp()))

                    Surface(
                        shape = RoundedCornerShape(10.sdp()),
                        color = Color.White,
                        border = BorderStroke(1.dp, categoryColor.copy(alpha = 0.3f)),
                        shadowElevation = 0.dp
                    ) {
                        Text(
                            text = stringResource(id = R.string.ration_items_count, group.items.size),
                            fontSize = 10.ssp(),
                            fontWeight = FontWeight.SemiBold,
                            color = categoryColor,
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier.padding(horizontal = 8.sdp(), vertical = 3.sdp())
                        )
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.sdp()),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFDDE7E3)),
                    shadowElevation = 0.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.sdp(), vertical = 4.sdp())
                    ) {
                        group.items.forEachIndexed { index, item ->
                            val isChecked = checkedItems.contains(item.name)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { onToggleItem(item.name) }
                                    .padding(vertical = 7.sdp()),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.sdp()),
                                    color = if (isChecked) BrandGreen else Color.White,
                                    border = BorderStroke(
                                        width = 1.5.dp,
                                        color = if (isChecked) BrandGreen else Color(0xFFCBD5E1)
                                    ),
                                    shadowElevation = 0.dp,
                                    modifier = Modifier.size(20.sdp())
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        if (isChecked) {
                                            Icon(
                                                imageVector = Icons.Rounded.Check,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(13.sdp())
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.width(10.sdp()))

                                Text(
                                    text = item.displayName(isHindi),
                                    fontSize = 13.ssp(),
                                    lineHeight = 17.ssp(),
                                    color = if (isChecked) TextSecondary else TextMain,
                                    textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )

                                Spacer(modifier = Modifier.width(8.sdp()))

                                Text(
                                    text = CateringCalculatorEngine.formatRawAmount(
                                        amount = item.totalAmount,
                                        unit = item.unit,
                                        isHindi = isHindi
                                    ),
                                    fontSize = 12.ssp(),
                                    fontWeight = FontWeight.Bold,
                                    color = if (isChecked) TextSecondary else BrandGreenDark,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }

                            if (index < group.items.lastIndex) {
                                HorizontalDivider(thickness = 1.dp, color = Color(0xFFF1F6F4))
                            }
                        }
                    }
                }
            }
        }
    }
}
