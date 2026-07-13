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

@Composable
fun TodayMarketScreen(
    state: MandiState,
    onEvent: (MandiEvent) -> Unit,
    effect: Flow<MandiEffect>,
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

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
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
            MandiTopBar(title = stringResource(id = R.string.today_market), onBackClick = { onEvent(MandiEvent.BackClick) })

            if (state.isLoading) {
                MandiTableSkeleton()
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.sdp(), vertical = 8.sdp()),
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
                                text = stringResource(id = R.string.vegetables),
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
                            items(state.marketPrices) { vegetable ->
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
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TodayMarketScreenPreview() {
    AapanGavTheme {
        TodayMarketScreen(
            state = MandiState(),
            onEvent = {},
            effect = kotlinx.coroutines.flow.emptyFlow(),
            onNavigateBack = {}
        )
    }
}
