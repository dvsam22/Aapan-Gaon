package com.dv.apna.feature.mandi.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.TrendingFlat
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import com.dv.apna.R
import com.dv.apna.core.components.MandiTableSkeleton
import com.dv.apna.core.theme.AapanGavTheme
import com.dv.apna.core.utils.sdp
import com.dv.apna.core.utils.ssp
import com.dv.apna.feature.mandi.presentation.effect.MandiEffect
import com.dv.apna.feature.mandi.presentation.event.MandiEvent
import com.dv.apna.feature.mandi.presentation.state.MandiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import com.dv.apna.core.components.AapanGavEmptyData
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Brush
import com.dv.apna.core.ads.BannerAdView
import com.dv.apna.core.components.AapanGavSearchBar
import com.dv.apna.core.config.RemoteConfigManager
import com.dv.apna.core.theme.MintGradientEnd
import com.dv.apna.core.theme.MintGradientMiddle
import com.dv.apna.core.theme.MintGradientStart
import kotlinx.coroutines.flow.emptyFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun TodayMarketScreen(
    state: MandiState,
    onEvent: (MandiEvent) -> Unit,
    effect: Flow<MandiEffect>,
    remoteConfigManager: RemoteConfigManager,
    onNavigateBack: () -> Unit,
) {
    LaunchedEffect(Unit) {
        effect.collectLatest { effect ->
            when (effect) {
                MandiEffect.NavigateBack -> onNavigateBack()
                else -> {}
            }
        }
    }

    val searchQueryState = rememberSaveable { mutableStateOf("") }
    val query = searchQueryState.value
    val filteredMarketPrices = remember(state.marketPrices, query) {
        if (query.isBlank()) {
            state.marketPrices
        } else {
            state.marketPrices.filter { item ->
                item.name.contains(query, ignoreCase = true) ||
                item.unit.contains(query, ignoreCase = true) ||
                item.price.toString().contains(query, ignoreCase = true)
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
            MandiTopBar(title = stringResource(id = R.string.today_mandi_prices), onBackClick = { onEvent(MandiEvent.BackClick) })
            AapanGavSearchBar(
                query = query,
                onQueryChange = { searchQueryState.value = it }
            )

            if (state.isLoading) {
                MandiTableSkeleton()
            } else if (filteredMarketPrices.isEmpty() && state.error == null) {
                AapanGavEmptyData(
                    message = if (query.isNotEmpty()) stringResource(id = R.string.no_results_found) else stringResource(id = R.string.no_records_found)
                )
            } else {
                val latestDate = filteredMarketPrices.maxOfOrNull { it.date } ?: 0L
                val dateLabel = formatMandiDate(latestDate)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.sdp(), vertical = 6.sdp()),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.sdp()),
                        color = Color(0xFFEFFAF6),
                        border = BorderStroke(1.sdp(), Color(0xFF2CA074).copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.sdp(), vertical = 6.sdp()),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.sdp())
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.clock),
                                contentDescription = null,
                                modifier = Modifier.size(13.sdp()),
                                tint = Color(0xFF2CA074)
                            )
                            Text(
                                text = "${stringResource(id = R.string.rates_updated)}: $dateLabel",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.ssp()
                                ),
                                color = Color(0xFF2CA074)
                            )
                        }
                    }

                    Text(
                        text = stringResource(id = R.string.available, filteredMarketPrices.size),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.ssp(),
                            fontWeight = FontWeight.Medium
                        ),
                        color = Color.Gray
                    )
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.sdp(), vertical = 4.sdp()),
                    shape = RoundedCornerShape(15.sdp()),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.sdp(), Color(0xFF2CA074).copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.sdp())) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.sdp()),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(id = R.string.crop_or_item),
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.ssp()
                                ),
                                color = Color.Black,
                                modifier = Modifier.weight(1.5f)
                            )
                            Text(
                                text = stringResource(id = R.string.unit),
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.ssp()
                                ),
                                color = Color.Black,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = stringResource(id = R.string.price),
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.ssp()
                                ),
                                color = Color.Black,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.End
                            )
                        }

                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.sdp())
                        ) {
                            items(filteredMarketPrices) { vegetable ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = vegetable.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontSize = 12.ssp(),
                                            fontWeight = FontWeight.Medium
                                        ),
                                        color = Color.Gray,
                                        modifier = Modifier.weight(1.5f)
                                    )
                                    Text(
                                        text = vegetable.unit,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontSize = 12.ssp(),
                                            fontWeight = FontWeight.Medium
                                        ),
                                        color = Color.Gray,
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.Center
                                    )
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "₹${vegetable.price}",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontSize = 12.ssp(),
                                                fontWeight = FontWeight.Medium
                                            ),
                                            color = Color.Gray,
                                            textAlign = TextAlign.End
                                        )

                                        Spacer(modifier = Modifier.width(4.sdp()))

                                        Icon(
                                            imageVector = when (vegetable.trend.lowercase()) {
                                                "up" -> Icons.Default.ArrowUpward
                                                "down" -> Icons.Default.ArrowDownward
                                                else -> Icons.Default.TrendingFlat
                                            },
                                            contentDescription = null,
                                            modifier = Modifier.size(12.sdp()),
                                            tint = when (vegetable.trend.lowercase()) {
                                                "up" -> Color(0xFF38C792)
                                                "down" -> Color.Red
                                                else -> Color.Gray
                                            }
                                        )
                                    }
                                }
                            }
                            item {
                                BannerAdView(remoteConfigManager = remoteConfigManager)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun formatMandiDate(dateMs: Long): String {
    if (dateMs <= 0L) return stringResource(id = R.string.today)
    val now = Calendar.getInstance()
    val itemCal = Calendar.getInstance().apply { timeInMillis = dateMs }

    val isSameYear = now.get(Calendar.YEAR) == itemCal.get(Calendar.YEAR)
    val isToday = isSameYear && now.get(Calendar.DAY_OF_YEAR) == itemCal.get(Calendar.DAY_OF_YEAR)
    val isYesterday = isSameYear && (now.get(Calendar.DAY_OF_YEAR) - itemCal.get(Calendar.DAY_OF_YEAR) == 1)

    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val formattedDateStr = dateFormat.format(Date(dateMs))

    return when {
        isToday -> "${stringResource(id = R.string.today)} ($formattedDateStr)"
        isYesterday -> "${stringResource(id = R.string.yesterday)} ($formattedDateStr)"
        else -> formattedDateStr
    }
}

@Preview(showBackground = true)
@Composable
fun TodayMarketScreenPreview() {
    AapanGavTheme {
        TodayMarketScreen(
            state = MandiState(),
            onEvent = {},
            effect = emptyFlow(),
            remoteConfigManager = RemoteConfigManager(),
            onNavigateBack = {}
        )
    }
}
