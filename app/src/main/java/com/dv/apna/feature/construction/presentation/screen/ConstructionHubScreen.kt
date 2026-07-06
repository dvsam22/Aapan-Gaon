package com.dv.apna.feature.construction.presentation.screen

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowCircleRight
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import com.dv.apna.R
import com.dv.apna.core.theme.AapanGavTheme
import com.dv.apna.core.utils.sdp
import com.dv.apna.core.utils.ssp
import com.dv.apna.feature.construction.presentation.effect.ConstructionEffect
import com.dv.apna.feature.construction.presentation.event.ConstructionEvent
import com.dv.apna.feature.construction.presentation.state.ConstructionState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ConstructionHubScreen(
    state: ConstructionState,
    onEvent: (ConstructionEvent) -> Unit,
    effect: Flow<ConstructionEffect>,
    onNavigateBack: () -> Unit,
    onNavigateToBricks: () -> Unit,
    onNavigateToMaterialShops: () -> Unit,
    onNavigateToHardwareShops: () -> Unit
) {
    LaunchedEffect(Unit) {
        effect.collectLatest { effect ->
            when (effect) {
                ConstructionEffect.NavigateBack -> onNavigateBack()
                ConstructionEffect.NavigateToBricks -> onNavigateToBricks()
                ConstructionEffect.NavigateToMaterialShops -> onNavigateToMaterialShops()
                ConstructionEffect.NavigateToHardwareShops -> onNavigateToHardwareShops()
            }
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        val (bottomImage, mainContent) = createRefs()

        // Bottom Decoration Image
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
                }
        ) {
            ConstructionTopBar(onBackClick = onNavigateBack)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.sdp(), vertical = 1.sdp()),
                verticalArrangement = Arrangement.spacedBy(12.sdp())
            ) {
                item {
                    ConstructionOptionCard(
                        title = "Bricks",
                        icon = painterResource(id = R.drawable.iv_bricks),
                        onClick = { onEvent(ConstructionEvent.BricksClick) }
                    )
                }
                item {
                    ConstructionOptionCard(
                        title = "Material Shops",
                        icon = painterResource(id = R.drawable.iv_material_shpos),
                        onClick = { onEvent(ConstructionEvent.MaterialShopsClick) }
                    )
                }
                item {
                    ConstructionOptionCard(
                        title = "Hardware Shops",
                        icon = painterResource(id = R.drawable.hardware_shops),
                        onClick = { onEvent(ConstructionEvent.HardwareShopsClick) }
                    )
                }
            }
        }
    }
}

@Composable
fun ConstructionTopBar(onBackClick: () -> Unit) {
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

        Text(
            text = "Construction Hub",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.ssp()
            ),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun ConstructionOptionCard(
    title: String,
    icon: androidx.compose.ui.graphics.painter.Painter,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(85.sdp())
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
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
                        text = title,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.ssp()
                        ),
                        color = Color.Black
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
fun ConstructionHubScreenPreview() {
    AapanGavTheme {
        ConstructionHubScreen(
            state = ConstructionState(),
            onEvent = {},
            effect = kotlinx.coroutines.flow.emptyFlow(),
            onNavigateBack = {},
            onNavigateToBricks = {},
            onNavigateToMaterialShops = {},
            onNavigateToHardwareShops = {}
        )
    }
}
