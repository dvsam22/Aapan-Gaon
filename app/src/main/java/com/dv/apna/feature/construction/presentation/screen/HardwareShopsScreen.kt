package com.dv.apna.feature.construction.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.dv.apna.R
import com.dv.apna.core.components.AapanGavErrorScreen
import com.dv.apna.core.components.AapanGavEmptyData
import com.dv.apna.core.components.ConstructionSkeleton
import com.dv.apna.core.theme.AapanGavTheme
import com.dv.apna.core.utils.sdp
import com.dv.apna.core.utils.ssp
import com.dv.apna.feature.construction.domain.model.HardwareShopModel
import com.dv.apna.feature.construction.presentation.effect.HardwareShopsEffect
import com.dv.apna.feature.construction.presentation.event.HardwareShopsEvent
import com.dv.apna.feature.construction.presentation.state.HardwareShopsState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.ui.platform.LocalContext
import com.dv.apna.core.utils.dial

@Composable
fun HardwareShopsScreen(
    state: HardwareShopsState,
    onEvent: (HardwareShopsEvent) -> Unit,
    effect: Flow<HardwareShopsEffect>,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        effect.collectLatest { effect ->
            when (effect) {
                is HardwareShopsEffect.NavigateBack -> onNavigateBack()
                is HardwareShopsEffect.DialPhone -> {
                    context.dial(effect.phone)
                }
            }
        }
    }


    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(androidx.compose.ui.graphics.Brush.verticalGradient(listOf(com.dv.apna.core.theme.MintGradientStart, com.dv.apna.core.theme.MintGradientMiddle, com.dv.apna.core.theme.MintGradientEnd)))
    ) {
        val (bottomImage, mainContent, loading, error) = createRefs()

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
            HardwareShopsTopBar(
                onBackClick = { onEvent(HardwareShopsEvent.BackClick) },
                availableCount = state.shops.size
            )

            if (state.isLoading) {
                ConstructionSkeleton()
            } else {
                if (state.shops.isEmpty() && state.error == null) {
                    AapanGavEmptyData(message = stringResource(id = R.string.no_shops_found))
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.sdp()),
                        verticalArrangement = Arrangement.spacedBy(10.sdp())
                    ) {
                        items(state.shops) { shop ->
                            HardwareShopCard(
                                shop = shop,
                                onCallClick = { onEvent(HardwareShopsEvent.CallClick(shop.phone)) }
                            )
                        }
                    }
                }
            }
        }

        if (state.error != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .constrainAs(error) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                contentAlignment = Alignment.Center
            ) {
                AapanGavErrorScreen(
                    message = state.error.asString(),
                    onRetry = { /* TODO: Refresh */ }
                )
            }
        }
    }
}

@Composable
fun HardwareShopsTopBar(onBackClick: () -> Unit, availableCount: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.sdp(), vertical = 16.sdp()),
        contentAlignment = Alignment.CenterStart
    ) {
        Surface(
            modifier = Modifier
                .width(34.sdp())
                .height(46.sdp())
                .clickable(
                    interactionSource = remember { MutableInteractionSource() }, indication = null
                ) { onBackClick() },
            shape = RoundedCornerShape(24.sdp()),
            color = Color(0xFFEFFAF6),
            border = BorderStroke(1.sdp(), Color(0xFFD9D9D9))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_left),
                    contentDescription = "Back",
                    modifier = Modifier.size(20.sdp()),
                    tint = Color.Black
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.hardware_shops), style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold, fontSize = 16.ssp()
                ), color = Color.Black
            )
            Text(
                text = stringResource(id = R.string.available, availableCount), style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.ssp(), color = Color.Black.copy(alpha = 0.6f)
                )
            )
        }
    }
}

@Composable
fun HardwareShopCard(
    shop: HardwareShopModel, onCallClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.sdp())
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
                            painter = painterResource(id = R.drawable.hardware_shops),
                            contentDescription = null,
                            modifier = Modifier.size(28.sdp()),
                            tint = Color(0xFF2CA074)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.sdp()))

                Column {
                    Text(
                        text = shop.name, style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium, fontSize = 14.ssp()
                        ), color = Color.Black
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.iv_location),
                            contentDescription = null,
                            modifier = Modifier.size(12.sdp()),
                            tint = Color(0xFF2CA074)
                        )
                        Spacer(modifier = Modifier.width(4.sdp()))
                        Text(
                            text = shop.address, style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.ssp(), color = Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    }
                }
            }

            if (shop.items.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.sdp()))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.receipt),
                        contentDescription = null,
                        modifier = Modifier.size(14.sdp()),
                        tint = Color(0xFF2CA074)
                    )
                    Spacer(modifier = Modifier.width(8.sdp()))
                    Text(
                        text = stringResource(id = R.string.items_available), style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium, fontSize = 12.ssp()
                        ), color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(8.sdp()))

                shop.items.forEach { item ->
                    Row(
                        modifier = Modifier.padding(start = 4.sdp(), bottom = 3.sdp()),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.sdp())
                                .background(Color(0xFF38C792), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.sdp()))
                        Text(
                            text = "${item.name} - ₹${item.price} / ${item.unit}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.ssp(), color = Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    }
                }
            }

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
                        text = stringResource(id = R.string.call_now), style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium, fontSize = 14.ssp()
                        ), color = Color.White
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HardwareShopsScreenPreview() {
    AapanGavTheme {
        HardwareShopsScreen(
            state = HardwareShopsState(),
            onEvent = {},
            effect = kotlinx.coroutines.flow.emptyFlow(),
            onNavigateBack = {})
    }
}
