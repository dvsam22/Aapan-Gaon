package com.dv.apna.feature.home.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import com.dv.apna.core.components.AapanGavLoading
import com.dv.apna.core.components.AapanGavErrorScreen
import com.dv.apna.core.theme.AapanGavTheme
import com.dv.apna.core.utils.sdp
import com.dv.apna.core.utils.ssp
import com.dv.apna.feature.home.presentation.state.HomeState
import com.dv.apna.feature.home.presentation.event.HomeEvent
import com.dv.apna.feature.home.domain.model.BannerModel
import com.dv.apna.R

@Composable
fun HomeScreen(
    state: HomeState,
    onEvent: (HomeEvent) -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToConstruction: () -> Unit,
    onNavigateToLabour: () -> Unit,
    onNavigateToTransport: () -> Unit,
    onNavigateToMandi: () -> Unit,
    onNavigateToNews: () -> Unit,
    onNavigateToHealth: () -> Unit,
) {
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
            HomeTopBar(onNotificationsClick = onNavigateToNotifications)

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    AapanGavLoading()
                }
            } else if (state.error != null) {
                AapanGavErrorScreen(
                    message = state.error,
                    onRetry = { onEvent(HomeEvent.Refresh) }
                )
            } else {
                HomeContent(
                    banners = state.banners,
                    onConstructionClick = onNavigateToConstruction,
                    onLabourClick = onNavigateToLabour,
                    onTransportClick = onNavigateToTransport,
                    onMandiClick = onNavigateToMandi,
                    onNewsClick = onNavigateToNews,
                    onHealthClick = onNavigateToHealth
                )
            }
        }
    }
}

@Composable
fun HomeTopBar(onNotificationsClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.sdp(), end = 16.sdp(), top = 16.sdp(), bottom = 8.sdp()),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { /* TODO: Open Drawer or Menu */ },
            modifier = Modifier.size(28.sdp())
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu",
                modifier = Modifier.size(28.sdp()),
                tint = Color.Black
            )
        }

        Surface(
            modifier = Modifier.size(38.sdp()),
            shape = CircleShape,
            color = Color(0xFFEFFAF6),
            border = BorderStroke(1.sdp(), Color(0xFFD9D9D9)),
            onClick = onNotificationsClick
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    modifier = Modifier.size(20.sdp()),
                    tint = Color(0xFF38C792)
                )
            }
        }
    }
}

@Composable
fun HomeContent(
    banners: List<BannerModel>,
    onConstructionClick: () -> Unit,
    onLabourClick: () -> Unit,
    onTransportClick: () -> Unit,
    onMandiClick: () -> Unit,
    onNewsClick: () -> Unit,
    onHealthClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.sdp())
    ) {
        item {
            BannerCarousel(banners = banners)
        }

        item {
            Text(
                text = "Services",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.ssp()
                ),
                modifier = Modifier.padding(start = 16.sdp(), top = 25.sdp(), bottom = 8.sdp())
            )
        }

        item {
            ServicesGrid(
                onConstructionClick = onConstructionClick,
                onLabourClick = onLabourClick,
                onTransportClick = onTransportClick,
                onMandiClick = onMandiClick,
                onNewsClick = onNewsClick,
                onHealthClick = onHealthClick
            )
        }
    }
}

@Composable
fun BannerCarousel(banners: List<BannerModel>) {
    if (banners.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { banners.size })

    Column {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(130.sdp()),
            contentPadding = PaddingValues(horizontal = 16.sdp()),
            pageSpacing = 8.sdp()
        ) { page ->
            val banner = banners[page]
            Card(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(12.sdp()),
                colors = CardDefaults.cardColors(
                    containerColor = try {
                        Color(android.graphics.Color.parseColor(banner.backgroundColor))
                    } catch (e: Exception) {
                        Color.White
                    }
                ),
                border = BorderStroke(1.sdp(), Color(0x1A000000))
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Content based on banner data
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.6f)
                            .padding(16.sdp()),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = banner.title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.ssp()
                            ),
                            color = Color.Black,
                            maxLines = 2
                        )
                        
                        if (banner.discountText.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.sdp()))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Disc. ",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Black
                                )
                                Text(
                                    text = banner.discountText,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFF59E0B)
                                    )
                                )
                            }
                        }
                    }

                    Image(
                        painter = painterResource(id = R.drawable.iv_dummy_banner),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
        
        // Page indicator dots
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.sdp()),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(banners.size) { iteration ->
                val color = if (pagerState.currentPage == iteration) Color(0xFF38C792) else Color(0xFFD8D8D8)
                val size = if (pagerState.currentPage == iteration) 8.sdp() else 6.sdp()
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.sdp())
                        .size(size)
                        .background(color, CircleShape)
                )
            }
        }
    }
}

@Composable
fun ServicesGrid(
    onConstructionClick: () -> Unit,
    onLabourClick: () -> Unit,
    onTransportClick: () -> Unit,
    onMandiClick: () -> Unit,
    onNewsClick: () -> Unit,
    onHealthClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.sdp()),
        verticalArrangement = Arrangement.spacedBy(8.sdp())
    ) {
        // Construction Hub
        ServiceCard(
            title = "Construction Hub",
            icon = Icons.Default.Engineering,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.sdp()),
            onClick = onConstructionClick
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.sdp())
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.sdp())) {
                ServiceCard(
                    title = "Labour Board",
                    icon = Icons.Default.Groups,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.sdp()),
                    onClick = onLabourClick
                )
                ServiceCard(
                    title = "Transport & Rentals",
                    icon = Icons.Default.Agriculture,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.sdp()),
                    onClick = onTransportClick
                )
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.sdp())) {
                ServiceCard(
                    title = "Mandi Hub",
                    icon = Icons.Default.Storefront,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.sdp()),
                    onClick = onMandiClick
                )
                ServiceCard(
                    title = "Local News",
                    icon = Icons.Default.Newspaper,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.sdp()),
                    onClick = onNewsClick
                )
            }
        }

        // Health & Emergency
        ServiceCard(
            title = "Health & Emergency",
            icon = Icons.Default.MedicalServices,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.sdp()),
            onClick = onHealthClick
        )
    }
}

@Composable
fun ServiceCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.sdp()),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.sdp()),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.sdp())
                .background(Color(0xFFEFFAF6), RoundedCornerShape(8.sdp())),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 4.sdp())
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(44.sdp()),
                    tint = Color(0xFF2CA074)
                )
                
                Spacer(modifier = Modifier.height(6.sdp()))
                
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.ssp()
                    ),
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    AapanGavTheme {
        HomeScreen(
            state = HomeState(),
            onEvent = {},
            onNavigateToNotifications = {},
            onNavigateToConstruction = {},
            onNavigateToLabour = {},
            onNavigateToTransport = {},
            onNavigateToMandi = {},
            onNavigateToNews = {},
            onNavigateToHealth = {}
        )
    }
}
