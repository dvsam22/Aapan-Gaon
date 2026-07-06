package com.dv.apna.feature.language.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.dv.apna.core.components.AapanGavButton
import com.dv.apna.core.theme.AapanGavTheme
import com.dv.apna.core.utils.sdp
import com.dv.apna.core.utils.ssp
import com.dv.apna.feature.language.domain.model.LanguageModel
import com.dv.apna.feature.language.presentation.effect.LanguageEffect
import com.dv.apna.R
import com.dv.apna.feature.language.presentation.event.LanguageEvent
import com.dv.apna.feature.language.presentation.state.LanguageState
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LanguageScreen(
    state: LanguageState,
    onEvent: (LanguageEvent) -> Unit,
    effect: kotlinx.coroutines.flow.Flow<LanguageEffect>,
    onNavigateToHome: () -> Unit
) {
    LaunchedEffect(Unit) {
        effect.collectLatest { effect ->
            when (effect) {
                LanguageEffect.NavigateToHome -> onNavigateToHome()
            }
        }
    }

    val contentTopMargin = 35.sdp()
    val contentHorizontalMargin = 18.sdp()
    val btnBottomMargin = 60.sdp()
    val btnHorizontalMargin = 16.sdp()

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        val (bottomImage, content, btnContinue) = createRefs()

        // Bottom Decoration Image - Stays at the very bottom, even under nav bar
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

        // Main Content - Uses statusBarsPadding for the top
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .constrainAs(content) {
                    top.linkTo(parent.top, margin = contentTopMargin)
                    start.linkTo(parent.start, margin = contentHorizontalMargin)
                    end.linkTo(parent.end, margin = contentHorizontalMargin)
                    width = Dimension.fillToConstraints
                }
        ) {
            Text(
                text = "Select Language/ भाषा चुने",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.ssp(),
                    lineHeight = 36.ssp()
                ),
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(20.sdp()))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.sdp())
            ) {
                state.languages.forEach { language ->
                    LanguageCard(
                        language = language,
                        isSelected = state.selectedLanguageId == language.id,
                        onClick = { onEvent(LanguageEvent.SelectLanguage(language.id)) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(35.sdp()))

            Text(
                text = "Select Village/ गाँव चुनें",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.ssp(),
                    lineHeight = 36.ssp()
                ),
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(20.sdp()))

            VillageSelector(
                villages = state.villages,
                selectedVillage = state.selectedVillage,
                onVillageSelected = { onEvent(LanguageEvent.SelectVillage(it)) }
            )
        }

        // Bottom Button - Uses navigationBarsPadding to stay above nav bar
        AapanGavButton(
            text = "Continue",
            onClick = { onEvent(LanguageEvent.Continue) },
            enabled = state.selectedLanguageId != null && state.selectedVillage != null,
            modifier = Modifier
                .navigationBarsPadding()
                .constrainAs(btnContinue) {
                    bottom.linkTo(parent.bottom, margin = btnBottomMargin)
                    start.linkTo(parent.start, margin = btnHorizontalMargin)
                    end.linkTo(parent.end, margin = btnHorizontalMargin)
                    width = Dimension.fillToConstraints
                }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VillageSelector(
    villages: List<String>,
    selectedVillage: String?,
    onVillageSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.sdp())
                .menuAnchor(MenuAnchorType.PrimaryEditable, true)
                .background(Color(0xFFEFFAF6), RoundedCornerShape(12.sdp()))
               /* .border(1.sdp(), Color(0xFF38C792), RoundedCornerShape(12.sdp()))*/
                .padding(horizontal = 16.sdp()),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = selectedVillage ?: "Select Here/ यहाँ चुनें",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 12.ssp(),
                        color = if (selectedVillage == null) Color(0xFF8391A1) else Color.Black
                    )
                )
                Icon(
                    painter = painterResource(R.drawable.arrow_left),
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(18.sdp()).rotate(-90f)
                )
            }
        }
        Spacer(modifier = Modifier.height(5.sdp()))
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .border(1.sdp(), Color(0xFF38C792), RoundedCornerShape(12.sdp())),
            containerColor = Color.White,
            shape = RoundedCornerShape(12.sdp())
        ) {
            if (villages.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("No villages found", color = Color.Gray) },
                    onClick = { expanded = false }
                )
            } else {
                villages.forEachIndexed { index, village ->
                    val isSelected = village == selectedVillage
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = village,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 12.ssp(),
                                        color = Color.Black
                                    )
                                )

                                // Radio Selection Indicator
                                Box(
                                    modifier = Modifier
                                        .size(16.sdp())
                                        .border(1.sdp(), Color(0xFF38C792), CircleShape)
                                        .padding(3.sdp())
                                ) {
                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(Color(0xFF38C792), CircleShape)
                                        )
                                    }
                                }
                            }
                        },
                        onClick = {
                            onVillageSelected(village)
                            expanded = false
                        }
                    )
                    if (index < villages.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 12.sdp()),
                            thickness = 1.sdp(),
                            color = Color(0xFF38C792).copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LanguageCard(
    language: LanguageModel,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = Color(0xFFEFFAF6)
    val borderColor = if (isSelected) Color(0xFF38C792) else Color.Transparent

    Box(
        modifier = modifier
            .aspectRatio(175.5f / 173f)
            .clip(RoundedCornerShape(15.sdp()))
            .background(backgroundColor)
            .border(
                border = BorderStroke(1.sdp(), borderColor),
                shape = RoundedCornerShape(15.sdp())
            )
            .clickable { onClick() }
    ) {
        // Selection Indicator (Radio circle)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.sdp())
                .size(16.sdp())
                .border(1.sdp(), Color(0xFF38C792), CircleShape)
                .padding(3.sdp())
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF38C792), CircleShape)
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(
                    id = if (language.code == "en") R.drawable.iv_english else R.drawable.iv_hindi
                ),
                contentDescription = language.name,
                modifier = Modifier.size(58.sdp()),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(5.sdp()))

            Text(
                text = if (language.code == "en") language.name else language.nativeName,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.ssp()
                ),
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LanguageScreenPreview() {
    AapanGavTheme {
        LanguageScreen(
            state = LanguageState(),
            onEvent = {},
            effect = kotlinx.coroutines.flow.emptyFlow(),
            onNavigateToHome = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LanguageScreenSelectedPreview() {
    AapanGavTheme {
        LanguageScreen(
            state = LanguageState(selectedLanguageId = "1"),
            onEvent = {},
            effect = kotlinx.coroutines.flow.emptyFlow(),
            onNavigateToHome = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LanguageScreenAllSelectedPreview() {
    AapanGavTheme {
        LanguageScreen(
            state = LanguageState(
                selectedLanguageId = "1",
                selectedVillage = "Maharajganj"
            ),
            onEvent = {},
            effect = kotlinx.coroutines.flow.emptyFlow(),
            onNavigateToHome = {}
        )
    }
}
