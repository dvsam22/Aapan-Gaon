package com.dv.apna.feature.transport.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
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
import com.dv.apna.core.components.TransportSkeleton
import com.dv.apna.core.theme.AapanGavTheme
import com.dv.apna.core.utils.sdp
import com.dv.apna.core.utils.ssp
import com.dv.apna.feature.transport.domain.model.TransportDetails
import com.dv.apna.feature.transport.presentation.effect.TransportEffect
import com.dv.apna.feature.transport.presentation.event.TransportEvent
import com.dv.apna.feature.transport.presentation.state.TransportState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember

@Composable
fun TransportDetailsScreen(
    state: TransportState,
    onEvent: (TransportEvent) -> Unit,
    effect: Flow<TransportEffect>,
    remoteConfigManager: com.dv.apna.core.config.RemoteConfigManager,
    onNavigateBack: () -> Unit
) {
    LaunchedEffect(Unit) {
        effect.collectLatest { effect ->
            when (effect) {
                TransportEffect.NavigateBack -> onNavigateBack()
                is TransportEffect.DialPhone -> {
                    // Handled in NavGraph
                }
                else -> {}
            }
        }
    }

    var searchQuery by androidx.compose.runtime.saveable.rememberSaveable { androidx.compose.runtime.mutableStateOf("") }
    val filteredTransportDetails = remember(state.transportDetails, searchQuery) {
        if (searchQuery.isBlank()) {
            state.transportDetails
        } else {
            state.transportDetails.filter { transport ->
                transport.name.contains(searchQuery, ignoreCase = true) ||
                transport.vehicleType.contains(searchQuery, ignoreCase = true) ||
                transport.location.contains(searchQuery, ignoreCase = true) ||
                transport.contact.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val nativeAd = com.dv.apna.core.ads.rememberNativeAd(
        adUnitId = remoteConfigManager.getNativeAdUnitId(com.dv.apna.core.config.ServiceAdCategory.TRANSPORT),
        isEnabled = remoteConfigManager.isNativeAdsEnabled()
    )

    val adPositions = remember(filteredTransportDetails.size) {
        val positions = mutableSetOf<Int>()
        if (filteredTransportDetails.size > 3) {
            var current = kotlin.random.Random.nextInt(4, 7)
            while (current < filteredTransportDetails.size) {
                positions.add(current)
                current += kotlin.random.Random.nextInt(6, 11)
            }
        } else if (filteredTransportDetails.isNotEmpty()) {
            positions.add(filteredTransportDetails.lastIndex)
        }
        positions
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(androidx.compose.ui.graphics.Brush.verticalGradient(listOf(com.dv.apna.core.theme.MintGradientStart, com.dv.apna.core.theme.MintGradientMiddle, com.dv.apna.core.theme.MintGradientEnd)))
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
            TransportDetailsTopBar(
                title = stringResource(id = R.string.details_title, title),
                availableCount = filteredTransportDetails.size,
                onBackClick = { onEvent(TransportEvent.BackClick) }
            )

            com.dv.apna.core.components.AapanGavSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it }
            )

            if (state.isLoading) {
                TransportSkeleton()
            } else {
                if (filteredTransportDetails.isEmpty() && state.error == null) {
                    AapanGavEmptyData(
                        message = if (searchQuery.isNotEmpty()) stringResource(id = R.string.no_results_found) else stringResource(id = R.string.no_vehicles_found)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 16.sdp(),
                            end = 16.sdp(),
                            top = 1.sdp(),
                            bottom = 80.sdp()
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.sdp())
                    ) {
                        itemsIndexed(
                            items = filteredTransportDetails,
                            key = { index, transport -> if (transport.id.isNotBlank()) transport.id else "${transport.name}_$index" }
                        ) { index, transport ->
                            TransportVehicleCard(
                                transport = transport,
                                onCallClick = { onEvent(TransportEvent.CallClick(transport.contact)) }
                            )

                            if (nativeAd != null && index in adPositions) {
                                Spacer(modifier = Modifier.height(12.sdp()))
                                com.dv.apna.core.ads.NativeAdCard(nativeAd = nativeAd)
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
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                AapanGavErrorScreen(message = state.error.asString(), onRetry = { /* TODO: Refresh */ })
            }
        }
    }
}

@Composable
fun TransportDetailsTopBar(
    title: String,
    availableCount: Int,
    onBackClick: () -> Unit
) {
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
                    contentDescription = "Back",
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
                text = title,
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
fun TransportVehicleCard(
    transport: TransportDetails,
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
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(32.sdp()),
                            tint = Color(0xFF38C792)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.sdp()))

                Column {
                    Text(
                        text = transport.name,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.ssp()
                        ),
                        color = Color.Black
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(12.sdp()),
                            tint = Color(0xFF2CA074)
                        )
                        Spacer(modifier = Modifier.width(4.sdp()))
                        Text(
                            text = transport.location,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.ssp(),
                                color = Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.sdp()))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.receipt),
                    contentDescription = null,
                    modifier = Modifier.size(14.sdp()),
                    tint = Color(0xFF2CA074)
                )
                Spacer(modifier = Modifier.width(8.sdp()))
                Text(
                    text = stringResource(id = R.string.vehicle_type_label),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.ssp()
                    ),
                    color = Color.Black
                )
                Spacer(modifier = Modifier.width(4.sdp()))
                Text(
                    text = transport.vehicleType,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.ssp(),
                        color = Color.Black.copy(alpha = 0.7f)
                    )
                )
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
fun TransportDetailsScreenPreview() {
    AapanGavTheme {
        TransportDetailsScreen(
            state = TransportState(
                selectedCategory = "Tractor",
                transportDetails = listOf(
                    TransportDetails(
                        name = "Sohan Singh",
                        location = "Rampur Village (Near Middle School)",
                        vehicleType = "Tractor - Mahindra 575",
                        contact = "1234567890"
                    )
                )
            ),
            onEvent = {},
            effect = kotlinx.coroutines.flow.emptyFlow(),
            remoteConfigManager = com.dv.apna.core.config.RemoteConfigManager(),
            onNavigateBack = {}
        )
    }
}
