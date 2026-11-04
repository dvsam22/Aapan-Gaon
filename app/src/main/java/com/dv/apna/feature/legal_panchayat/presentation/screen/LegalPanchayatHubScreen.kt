package com.dv.apna.feature.legal_panchayat.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import com.dv.apna.R
import com.dv.apna.core.ads.BannerAdView
import com.dv.apna.core.config.RemoteConfigManager
import com.dv.apna.core.theme.AapanGavTheme
import com.dv.apna.core.theme.MintGradientEnd
import com.dv.apna.core.theme.MintGradientMiddle
import com.dv.apna.core.theme.MintGradientStart
import com.dv.apna.core.utils.sdp
import com.dv.apna.core.utils.ssp

@Composable
fun LegalPanchayatHubScreen(
    remoteConfigManager: RemoteConfigManager,
    onNavigateBack: () -> Unit,
    onNavigateToDetails: (categoryId: String, title: String) -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(MintGradientStart, MintGradientMiddle, MintGradientEnd)))
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
                .navigationBarsPadding()
                .constrainAs(mainContent) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            LegalPanchayatTopBar(onBackClick = onNavigateBack)

            val courtLawyersTitle = stringResource(id = R.string.legal_court_lawyers)
            val panchayatOfficialsTitle = stringResource(id = R.string.legal_panchayat_officials)
            val seniorCitizensTitle = stringResource(id = R.string.legal_senior_citizens)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.sdp(), vertical = 8.sdp()),
                verticalArrangement = Arrangement.spacedBy(12.sdp())
            ) {
                item {
                    LegalPanchayatOptionCard(
                        title = courtLawyersTitle,
                        subtitle = stringResource(id = R.string.legal_court_lawyers_desc),
                        icon = painterResource(id = R.drawable.ic_lawyer),
                        onClick = { onNavigateToDetails("court_lawyers", courtLawyersTitle) }
                    )
                }
                item {
                    LegalPanchayatOptionCard(
                        title = panchayatOfficialsTitle,
                        subtitle = stringResource(id = R.string.legal_panchayat_officials_desc),
                        icon = painterResource(id = R.drawable.ic_panchayat_leader),
                        onClick = { onNavigateToDetails("panchayat_officials", panchayatOfficialsTitle) }
                    )
                }
                item {
                    LegalPanchayatOptionCard(
                        title = seniorCitizensTitle,
                        subtitle = stringResource(id = R.string.legal_senior_citizens_desc),
                        icon = painterResource(id = R.drawable.ic_elders_respected),
                        onClick = { onNavigateToDetails("senior_citizens", seniorCitizensTitle) }
                    )
                }
                item {
                    BannerAdView(remoteConfigManager = remoteConfigManager)
                }
            }
        }
    }
}

@Composable
fun LegalPanchayatTopBar(onBackClick: () -> Unit) {
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

        Text(
            text = stringResource(id = R.string.legal_panchayat_hub),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.ssp()
            ),
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 44.sdp())
        )
    }
}

@Composable
fun LegalPanchayatOptionCard(
    title: String,
    subtitle: String? = null,
    icon: Painter,
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
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
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

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.ssp(),
                                lineHeight = 16.ssp()
                            ),
                            color = Color.Black,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (!subtitle.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(2.sdp()))
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.ssp(),
                                    fontWeight = FontWeight.Normal
                                ),
                                color = Color.Black.copy(alpha = 0.6f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.sdp()))

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
fun LegalPanchayatHubScreenPreview() {
    AapanGavTheme {
        LegalPanchayatHubScreen(
            remoteConfigManager = RemoteConfigManager(),
            onNavigateBack = {},
            onNavigateToDetails = { _, _ -> }
        )
    }
}
