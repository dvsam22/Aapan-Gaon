package com.dv.apna.feature.construction.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.dv.apna.feature.construction.domain.model.BricksSupplierModel
import com.dv.apna.feature.construction.presentation.effect.BricksEffect
import com.dv.apna.feature.construction.presentation.event.BricksEvent
import com.dv.apna.feature.construction.presentation.state.BricksState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.ui.platform.LocalContext
import com.dv.apna.core.utils.dial

@Composable
fun BricksSuppliersScreen(
    state: BricksState,
    onEvent: (BricksEvent) -> Unit,
    effect: Flow<BricksEffect>,
    remoteConfigManager: com.dv.apna.core.config.RemoteConfigManager,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        effect.collectLatest { effect ->
            when (effect) {
                is BricksEffect.NavigateBack -> onNavigateBack()
                is BricksEffect.DialPhone -> {
                    context.dial(effect.phone)
                }
            }
        }
    }

    val searchQueryState = androidx.compose.runtime.saveable.rememberSaveable { androidx.compose.runtime.mutableStateOf("") }
    val query = searchQueryState.value
    val filteredSuppliers = remember(state.suppliers, query) {
        if (query.isBlank()) {
            state.suppliers
        } else {
            state.suppliers.filter { s ->
                s.name.contains(query, ignoreCase = true) ||
                s.brickTypes.any { it.name.contains(query, ignoreCase = true) } ||
                s.address.contains(query, ignoreCase = true) ||
                s.phone.contains(query, ignoreCase = true)
            }
        }
    }

    val nativeAd = com.dv.apna.core.ads.rememberNativeAd(
        adUnitId = remoteConfigManager.getNativeAdUnitId(com.dv.apna.core.config.ServiceAdCategory.CONSTRUCTION),
        isEnabled = remoteConfigManager.isNativeAdsEnabled()
    )

    val adPositions = remember(filteredSuppliers.size) {
        val positions = mutableSetOf<Int>()
        if (filteredSuppliers.size > 3) {
            var current = kotlin.random.Random.nextInt(4, 7)
            while (current < filteredSuppliers.size) {
                positions.add(current)
                current += kotlin.random.Random.nextInt(6, 11)
            }
        } else if (filteredSuppliers.isNotEmpty()) {
            positions.add(filteredSuppliers.lastIndex)
        }
        positions
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(
                androidx.compose.ui.graphics.Brush.verticalGradient(
                    listOf(
                        com.dv.apna.core.theme.MintGradientStart,
                        com.dv.apna.core.theme.MintGradientMiddle,
                        com.dv.apna.core.theme.MintGradientEnd
                    )
                )
            )
    ) {
        val (bottomImage, mainContent, error) = createRefs()

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
            BricksTopBar(
                onBackClick = { onEvent(BricksEvent.BackClick) },
                availableCount = filteredSuppliers.size
            )

            com.dv.apna.core.components.AapanGavSearchBar(
                query = query,
                onQueryChange = { searchQueryState.value = it }
            )

            if (state.isLoading) {
                ConstructionSkeleton()
            } else {
                if (filteredSuppliers.isEmpty() && state.error == null) {
                    AapanGavEmptyData(
                        message = if (query.isNotEmpty()) stringResource(id = R.string.no_results_found) else stringResource(id = R.string.no_suppliers)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.sdp()),
                        verticalArrangement = Arrangement.spacedBy(10.sdp())
                    ) {
                        itemsIndexed(
                            items = filteredSuppliers,
                            key = { index, supplier -> if (supplier.id.isNotBlank()) supplier.id else "${supplier.name}_$index" }
                        ) { index, supplier ->
                            BricksSupplierCard(
                                supplier = supplier,
                                onCallClick = { onEvent(BricksEvent.CallClick(supplier.phone)) }
                            )

                            if (nativeAd != null && index in adPositions) {
                                Spacer(modifier = Modifier.height(10.sdp()))
                                com.dv.apna.core.ads.NativeAdCard(
                                    nativeAd = nativeAd,
                                    modifier = Modifier.padding(horizontal = 16.sdp())
                                )
                            }
                        }
                        item(key = "banner_ad_item") {
                            com.dv.apna.core.ads.BannerAdView(remoteConfigManager = remoteConfigManager)
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
                    onRetry = { onEvent(BricksEvent.Refresh) }
                )
            }
        }
    }
}

@Composable
fun BricksTopBar(onBackClick: () -> Unit, availableCount: Int) {
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
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.bricks_suppliers),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.ssp()
                ),
                color = Color.Black
            )
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
fun BricksSupplierCard(
    supplier: BricksSupplierModel,
    onCallClick: () -> Unit
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
                            painter = painterResource(id = R.drawable.iv_bricks),
                            contentDescription = null,
                            modifier = Modifier.size(28.sdp()),
                            tint = Color(0xFF2CA074)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.sdp()))

                Column {
                    Text(
                        text = supplier.name,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.ssp()
                        ),
                        color = Color.Black
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
                            text = supplier.address,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.ssp(),
                                color = Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    }
                }
            }

            if (supplier.brickTypes.isNotEmpty()) {
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
                        text = stringResource(id = R.string.brick_types),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.ssp()
                        ),
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(8.sdp()))

                supplier.brickTypes.forEach { brickType ->
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
                            text = "${brickType.name} - ₹${brickType.price} / ${brickType.unit}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.ssp(),
                                color = Color.Black.copy(alpha = 0.7f)
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

@Preview(showBackground = true)
@Composable
fun BricksSuppliersScreenPreview() {
    AapanGavTheme {
        BricksSuppliersScreen(
            state = BricksState(),
            onEvent = {},
            effect = kotlinx.coroutines.flow.emptyFlow(),
            remoteConfigManager = com.dv.apna.core.config.RemoteConfigManager(),
            onNavigateBack = {}
        )
    }
}
