package com.dv.apna.feature.transport.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import com.dv.apna.R
import com.dv.apna.core.common.UiText
import com.dv.apna.core.components.AapanGavEmptyData
import com.dv.apna.core.theme.AapanGavTheme
import com.dv.apna.core.utils.sdp
import com.dv.apna.core.utils.ssp
import com.dv.apna.feature.transport.domain.model.TransportService
import com.dv.apna.feature.transport.presentation.effect.TransportEffect
import com.dv.apna.feature.transport.presentation.event.TransportEvent
import com.dv.apna.feature.transport.presentation.state.TransportState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun TransportBoardScreen(
    state: TransportState,
    onEvent: (TransportEvent) -> Unit,
    effect: Flow<TransportEffect>,
    remoteConfigManager: com.dv.apna.core.config.RemoteConfigManager,
    onNavigateBack: () -> Unit,
    onNavigateToCategory: (String, String) -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        effect.collectLatest { effect ->
            when (effect) {
                TransportEffect.NavigateBack -> onNavigateBack()
                is TransportEffect.NavigateToCategory -> onNavigateToCategory(
                    effect.categoryId, effect.categoryName
                )

                else -> {}
            }
        }
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
                .constrainAs(mainContent) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }) {
            TransportTopBar(onBackClick = onNavigateBack)

            if (state.services.isEmpty() && !state.isLoading) {
                AapanGavEmptyData(message = stringResource(id = R.string.no_records_found))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(
                        start = 16.sdp(), end = 16.sdp(), top = 1.sdp(), bottom = 80.sdp()
                    ), verticalArrangement = Arrangement.spacedBy(12.sdp())
                ) {
                    items(state.services) { service ->
                        TransportOptionCard(
                            title = service.title.asString(),
                            icon = painterResource(id = service.icon),
                            onClick = {
                                onEvent(
                                    TransportEvent.CategoryClick(
                                        service.categoryId, service.title.asString(context)
                                    )
                                )
                            })
                    }
                    item {
                        com.dv.apna.core.ads.BannerAdView(remoteConfigManager = remoteConfigManager)
                    }
                }
            }
        }
    }
}

@Composable
fun TransportTopBar(onBackClick: () -> Unit) {
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

        Text(
            text = stringResource(id = R.string.transport_rentals),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold, fontSize = 16.ssp()
            ),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun TransportOptionCard(
    title: String, icon: androidx.compose.ui.graphics.painter.Painter, onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(85.sdp())
            .clickable(
                interactionSource = remember { MutableInteractionSource() }, indication = null
            ) { onClick() },
        shape = RoundedCornerShape(12.sdp()),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.sdp())
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.sdp())
                .background(Color(0xFFEFFAF6), RoundedCornerShape(8.sdp())),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.sdp()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(50.sdp()),
                        shape = RoundedCornerShape(10.sdp()),
                        color = Color.White,
                        border = BorderStroke(1.sdp(), Color(0xFF38C792).copy(alpha = 0.4f))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Image(
                                painter = icon,
                                contentDescription = null,
                                modifier = Modifier.size(28.sdp())
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.sdp()))

                    Text(
                        text = title, style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium, fontSize = 14.ssp()
                        ), color = Color.Black
                    )
                }

                Icon(
                    painter = painterResource(id = R.drawable.arrow_circle_right),
                    contentDescription = null,
                    modifier = Modifier.size(20.sdp()),
                    tint = Color(0xFF2CA074)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TransportBoardScreenPreview() {
    AapanGavTheme {
        TransportBoardScreen(
            state = TransportState(
            services = listOf(
                TransportService(
                    UiText.StringResource(R.string.tractor), R.drawable.transport, "tractor"
                ),
                TransportService(UiText.StringResource(R.string.car), R.drawable.iv_car, "car"),
                TransportService(
                    UiText.StringResource(R.string.pickup), R.drawable.pickup, "pickup"
                )
            )
        ),
            onEvent = {},
            effect = kotlinx.coroutines.flow.emptyFlow(),
            remoteConfigManager = com.dv.apna.core.config.RemoteConfigManager(),
            onNavigateBack = {},
            onNavigateToCategory = { _, _ -> })
    }
}
