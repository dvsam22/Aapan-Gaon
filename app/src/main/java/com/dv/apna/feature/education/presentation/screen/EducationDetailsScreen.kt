package com.dv.apna.feature.education.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.dv.apna.R
import com.dv.apna.core.ads.BannerAdView
import com.dv.apna.core.components.AapanGavEmptyData
import com.dv.apna.core.components.AapanGavErrorScreen
import com.dv.apna.core.components.AapanGavSearchBar
import com.dv.apna.core.components.LabourSkeleton
import com.dv.apna.core.config.RemoteConfigManager
import com.dv.apna.core.theme.AapanGavTheme
import com.dv.apna.core.theme.MintGradientEnd
import com.dv.apna.core.theme.MintGradientMiddle
import com.dv.apna.core.theme.MintGradientStart
import com.dv.apna.core.utils.sdp
import com.dv.apna.core.utils.ssp
import com.dv.apna.feature.education.domain.model.EducationDetails
import com.dv.apna.feature.education.presentation.effect.EducationEffect
import com.dv.apna.feature.education.presentation.event.EducationEvent
import com.dv.apna.feature.education.presentation.state.EducationState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun EducationDetailsScreen(
    state: EducationState,
    onEvent: (EducationEvent) -> Unit,
    effect: Flow<EducationEffect>,
    remoteConfigManager: RemoteConfigManager,
    onNavigateBack: () -> Unit,
    onNavigateToAddBusiness: (() -> Unit)? = null
) {
    LaunchedEffect(Unit) {
        effect.collectLatest { eff ->
            when (eff) {
                is EducationEffect.NavigateBack -> onNavigateBack()
                else -> {}
            }
        }
    }

    val searchQueryState = rememberSaveable { mutableStateOf("") }
    val query = searchQueryState.value
    val filteredItems = remember(state.items, query) {
        if (query.isBlank()) {
            state.items
        } else {
            state.items.filter { item ->
                item.name.contains(query, ignoreCase = true) ||
                item.details.contains(query, ignoreCase = true) ||
                item.location.contains(query, ignoreCase = true) ||
                item.extraInfo.contains(query, ignoreCase = true) ||
                item.contact.contains(query, ignoreCase = true)
            }
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(MintGradientStart, MintGradientMiddle, MintGradientEnd)))
    ) {
        val (bottomImage, mainContent) = createRefs()

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
                .constrainAs(mainContent) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            val title = state.selectedCategoryTitle?.asString() ?: state.selectedCategory
            EducationDetailsTopBar(
                title = stringResource(id = R.string.details_title, title),
                availableCount = filteredItems.size,
                onBackClick = { onEvent(EducationEvent.BackClick) }
            )

            AapanGavSearchBar(
                query = query,
                onQueryChange = { searchQueryState.value = it }
            )

            if (state.isLoading) {
                LabourSkeleton()
            } else {
                if (filteredItems.isEmpty() && state.error == null) {
                    AapanGavEmptyData(
                        message = if (query.isNotBlank()) {
                            stringResource(id = R.string.no_results_found)
                        } else {
                            stringResource(id = R.string.no_records_found)
                        },
                        onActionClick = onNavigateToAddBusiness
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 16.sdp(),
                            end = 16.sdp(),
                            top = 12.sdp(),
                            bottom = 80.sdp()
                        ),
                        verticalArrangement = Arrangement.spacedBy(16.sdp())
                    ) {
                        itemsIndexed(
                            items = filteredItems,
                            key = { index, item -> if (item.id.isNotBlank()) item.id else "${item.name}_$index" }
                        ) { _, item ->
                            EducationItemCard(
                                item = item,
                                onCallClick = { onEvent(EducationEvent.CallClick(item.contact)) }
                            )
                        }
                        item(key = "banner_ad_item") {
                            BannerAdView(remoteConfigManager = remoteConfigManager)
                        }
                    }
                }
            }
        }

        if (state.error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                AapanGavErrorScreen(
                    message = state.error.asString(),
                    onRetry = { onEvent(EducationEvent.Refresh) }
                )
            }
        }
    }
}

@Composable
fun EducationDetailsTopBar(
    title: String,
    availableCount: Int,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.sdp(), vertical = 16.sdp()),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .width(34.sdp())
                .height(46.sdp())
                .align(Alignment.CenterStart)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onBackClick() },
            shape = RoundedCornerShape(24.sdp()),
            color = Color(0xFFEFFAF6),
            border = BorderStroke(1.sdp(), Color(0xFFD9D9D9))
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

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 48.sdp())
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.ssp()
                ),
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.sdp()))
            Text(
                text = stringResource(id = R.string.available, availableCount),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.ssp(),
                    color = Color.Black.copy(alpha = 0.6f)
                )
            )
        }
    }
}

@Composable
fun EducationItemCard(
    item: EducationDetails,
    onCallClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.sdp(),
                shape = RoundedCornerShape(15.sdp()),
                clip = false,
                ambientColor = Color.Black.copy(alpha = 0.3f),
                spotColor = Color.Black.copy(alpha = 0.3f)
            ),
        shape = RoundedCornerShape(15.sdp()),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.sdp(), Color(0xFF2CA074).copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.sdp())
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(50.sdp()),
                    shape = RoundedCornerShape(12.sdp()),
                    color = Color(0xFFEEF7F6),
                    border = BorderStroke(1.sdp(), Color(0xFF38C792).copy(alpha = 0.4f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(id = R.drawable.person),
                            contentDescription = null,
                            modifier = Modifier.size(32.sdp()),
                            tint = Color(0xFF38C792)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.sdp()))

                Column {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.ssp()
                        ),
                        color = Color.Black
                    )
                    if (item.location.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.sdp()))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.iv_location),
                                contentDescription = null,
                                modifier = Modifier.size(12.sdp()),
                                tint = Color(0xFF2CA074)
                            )
                            Spacer(modifier = Modifier.width(4.sdp()))
                            Text(
                                text = item.location,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.ssp(),
                                    color = Color.Black.copy(alpha = 0.7f)
                                )
                            )
                        }
                    }
                }
            }

            if (item.details.isNotBlank()) {
                Spacer(modifier = Modifier.height(14.sdp()))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.receipt),
                        contentDescription = null,
                        modifier = Modifier.size(14.sdp()),
                        tint = Color(0xFF2CA074)
                    )
                    Spacer(modifier = Modifier.width(8.sdp()))
                    Text(
                        text = item.details,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.ssp(),
                            color = Color.Black.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Normal
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (item.extraInfo.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.sdp()))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.clock),
                        contentDescription = null,
                        modifier = Modifier.size(14.sdp()),
                        tint = Color(0xFF2CA074)
                    )
                    Spacer(modifier = Modifier.width(8.sdp()))
                    Text(
                        text = item.extraInfo,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.ssp(),
                            color = Color(0xFF2CA074),
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (item.contact.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.sdp()))
                Button(
                    onClick = onCallClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.sdp()),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38C792)),
                    shape = RoundedCornerShape(27.sdp()),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.iv_call),
                            contentDescription = null,
                            modifier = Modifier.size(18.sdp()),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.sdp()))
                        Text(
                            text = stringResource(id = R.string.call_now),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.ssp()
                            ),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EducationDetailsScreenPreview() {
    AapanGavTheme {
        EducationDetailsScreen(
            state = EducationState(),
            onEvent = {},
            effect = emptyFlow(),
            remoteConfigManager = RemoteConfigManager(),
            onNavigateBack = {}
        )
    }
}
