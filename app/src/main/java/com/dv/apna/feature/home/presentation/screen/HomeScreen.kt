package com.dv.apna.feature.home.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import com.dv.apna.core.components.AapanGavErrorScreen
import com.dv.apna.core.components.BannerSkeleton
import com.dv.apna.core.theme.AapanGavTheme
import com.dv.apna.core.utils.sdp
import com.dv.apna.core.utils.ssp
import com.dv.apna.core.utils.shareApp
import com.dv.apna.core.utils.rateApp
import com.dv.apna.feature.home.presentation.state.HomeState
import com.dv.apna.feature.home.presentation.event.HomeEvent
import com.dv.apna.feature.home.domain.model.BannerModel
import com.dv.apna.R
import androidx.annotation.DrawableRes
import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.launch
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
    onNavigateToFamilyFunction: () -> Unit,
    onNavigateToLanguage: () -> Unit,
    onNavigateToChangeVillage: () -> Unit,
    onNavigateToAboutUs: () -> Unit,
    onNavigateToPrivacyPolicy: () -> Unit,
    onNavigateToTerms: () -> Unit,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            // Handle result if needed
        }
    )

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.fillMaxWidth(0.70f),
                drawerContainerColor = Color.White,
                drawerShape = RoundedCornerShape(topEnd = 0.sdp(), bottomEnd = 0.sdp())
            ) {
                HomeDrawer(
                    onCloseClick = {
                        scope.launch { drawerState.close() }
                    },
                    onItemClick = { item ->
                        scope.launch { drawerState.close() }
                        when (item) {
                            DrawerItem.Language -> onNavigateToLanguage()
                            DrawerItem.Village -> onNavigateToChangeVillage()
                            DrawerItem.AboutUs -> onNavigateToAboutUs()
                            DrawerItem.PrivacyPolicy -> onNavigateToPrivacyPolicy()
                            DrawerItem.Terms -> onNavigateToTerms()
                            DrawerItem.Share -> context.shareApp()
                            DrawerItem.RateUs -> context.rateApp()
                        }
                    }
                )
            }
        },
        gesturesEnabled = true
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
                HomeTopBar(
                    villageName = state.selectedVillage,
                    onNotificationsClick = onNavigateToNotifications,
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    }
                )

                if (state.error != null) {
                    AapanGavErrorScreen(
                        message = state.error.asString(),
                        onRetry = { onEvent(HomeEvent.Refresh) }
                    )
                } else {
                    HomeContent(
                        isLoading = state.isLoading,
                        banners = state.banners,
                        onConstructionClick = onNavigateToConstruction,
                        onLabourClick = onNavigateToLabour,
                        onTransportClick = onNavigateToTransport,
                        onMandiClick = onNavigateToMandi,
                        onNewsClick = onNavigateToNews,
                        onHealthClick = onNavigateToHealth,
                        onFamilyFunctionClick = onNavigateToFamilyFunction
                    )
                }
            }
        }
    }
}

@Composable
fun HomeDrawer(
    onCloseClick: () -> Unit,
    onItemClick: (DrawerItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.sdp()),
            contentAlignment = Alignment.CenterStart
        ) {
            // App Logo
            Image(
                painter = painterResource(id = R.drawable.iv_splash_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(65.sdp())
                    .clip(CircleShape),
                contentScale = ContentScale.Fit
            )

            Surface(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(28.sdp())
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onCloseClick() },
                shape = CircleShape,
                color = Color.White,
                border = BorderStroke(1.sdp(), Color(0xFFD9D9D9))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        modifier = Modifier.size(16.sdp()),
                        tint = Color.Black
                    )
                }
            }
        }

        DrawerMenuItem(
            title = stringResource(id = R.string.change_language),
            onClick = { onItemClick(DrawerItem.Language) }
        )
        DrawerMenuItem(
            title = stringResource(id = R.string.change_village),
            onClick = { onItemClick(DrawerItem.Village) }
        )
        DrawerMenuItem(
            title = stringResource(id = R.string.about_us),
            onClick = { onItemClick(DrawerItem.AboutUs) }
        )
        DrawerMenuItem(
            title = stringResource(id = R.string.privacy_policy),
            onClick = { onItemClick(DrawerItem.PrivacyPolicy) }
        )
        DrawerMenuItem(
            title = stringResource(id = R.string.terms_and_conditions),
            onClick = { onItemClick(DrawerItem.Terms) }
        )
        DrawerMenuItem(
            title = stringResource(id = R.string.share_app),
            onClick = { onItemClick(DrawerItem.Share) }
        )
        DrawerMenuItem(
            title = stringResource(id = R.string.rate_us),
            onClick = { onItemClick(DrawerItem.RateUs) }
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = stringResource(id = R.string.version_format, "1.1"),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 12.ssp(),
                color = Color(0xFF8391A1)
            ),
            modifier = Modifier.padding(horizontal = 24.sdp(), vertical = 20.sdp())
        )
    }
}

@Composable
fun DrawerMenuItem(
    title: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.sdp(), vertical = 12.sdp()),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.ssp()
                ),
                color = Color.Black
            )
            Icon(
                painter = painterResource(id = R.drawable.arrow_circle_right),
                contentDescription = null,
                modifier = Modifier.size(20.sdp()),
                tint = Color(0xFF38C792)
            )
        }
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 24.sdp()),
            thickness = 1.sdp(),
            color = Color(0xFFF1F4F7)
        )
    }
}

enum class DrawerItem {
    Language, Village, AboutUs, PrivacyPolicy, Terms, Share, RateUs
}

@Composable
fun HomeTopBar(
    villageName: String?,
    onNotificationsClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.sdp(), end = 16.sdp(), top = 16.sdp(), bottom = 8.sdp()),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.sdp())
        ) {
            Icon(
                painter = painterResource(R.drawable.left),
                contentDescription = "Menu",
                modifier = Modifier
                    .size(24.sdp())
                    .clickable { onMenuClick() },
                tint = Color.Black
            )

            if (villageName != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.sdp())
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.location_pin),
                        contentDescription = null,
                        modifier = Modifier.size(20.sdp())
                    )
                    Text(
                        text = villageName,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.ssp()
                        ),
                        color = Color.Black
                    )
                }
            }
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
    isLoading: Boolean,
    banners: List<BannerModel>,
    onConstructionClick: () -> Unit,
    onLabourClick: () -> Unit,
    onTransportClick: () -> Unit,
    onMandiClick: () -> Unit,
    onNewsClick: () -> Unit,
    onHealthClick: () -> Unit,
    onFamilyFunctionClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.sdp(), top = 10.sdp())
    ) {
        item {
            if (isLoading) {
                BannerSkeleton()
            } else {
                BannerCarousel(banners = banners)
            }
        }

        item {
            Text(
                text = stringResource(id = R.string.services),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.ssp()
                ),
                modifier = Modifier.padding(start = 16.sdp(), top = 15.sdp(), bottom = 8.sdp())
            )
        }

        item {
            ServicesGrid(
                onConstructionClick = onConstructionClick,
                onLabourClick = onLabourClick,
                onTransportClick = onTransportClick,
                onMandiClick = onMandiClick,
                onNewsClick = onNewsClick,
                onHealthClick = onHealthClick,
                onFamilyFunctionClick = onFamilyFunctionClick
            )
        }
    }
}

@Composable
fun BannerCarousel(banners: List<BannerModel>) {
    if (banners.isEmpty()) return

    val realSize = banners.size
    // Starting from a large number for infinite scroll
    val initialPage = 500 * realSize
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { if (realSize > 1) Int.MAX_VALUE else 1 }
    )

    Column {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(130.sdp()),
            contentPadding = PaddingValues(horizontal = 16.sdp()),
            pageSpacing = 8.sdp()
        ) { page ->
            val banner = banners[page % realSize]
            Card(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(12.sdp()),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.sdp(), Color(0x1A000000))
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = banner.imageUrl,
                        contentDescription = banner.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.iv_dummy_banner),
                        error = painterResource(id = R.drawable.iv_dummy_banner)
                    )

                    // Overlay for content if needed
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.9f),
                                        Color.White.copy(alpha = 0.4f),
                                        Color.Transparent
                                    )
                                )
                            )
                            .padding(16.sdp()),
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (banner.title.isNotEmpty()) {
                            Text(
                                text = banner.title,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.ssp()
                                ),
                                color = Color.Black,
                                modifier = Modifier.fillMaxWidth(0.6f)
                            )
                        }

                        if (banner.discountText.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.sdp()))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = stringResource(id = R.string.discount_label),
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
            repeat(realSize) { iteration ->
                val color = if (pagerState.currentPage % realSize == iteration) Color(0xFF38C792) else Color(0xFFD8D8D8)
                val size = if (pagerState.currentPage % realSize == iteration) 8.sdp() else 6.sdp()
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
    onHealthClick: () -> Unit,
    onFamilyFunctionClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.sdp()),
        verticalArrangement = Arrangement.spacedBy(8.sdp())
    ) {
        // Construction Hub
        ServiceCard(
            title = stringResource(id = R.string.construction_hub),
            icon = R.drawable.construction,
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
                    title = stringResource(id = R.string.labour_board),
                    icon = R.drawable.labour,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.sdp()),
                    onClick = onLabourClick
                )
                ServiceCard(
                    title = stringResource(id = R.string.transport_rentals),
                    icon = R.drawable.transport,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.sdp()),
                    onClick = onTransportClick
                )
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.sdp())) {
                ServiceCard(
                    title = stringResource(id = R.string.mandi_hub),
                    icon = R.drawable.mandi,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.sdp()),
                    onClick = onMandiClick
                )
                ServiceCard(
                    title = stringResource(id = R.string.local_news),
                    icon = R.drawable.local_news,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.sdp()),
                    onClick = onNewsClick
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.sdp())
        ) {
            // Health & Emergency
            ServiceCard(
                title = stringResource(id = R.string.health_emergency),
                icon = R.drawable.health,
                modifier = Modifier
                    .weight(1f)
                    .height(150.sdp()),
                onClick = onHealthClick
            )

            // Family Function
            ServiceCard(
                title = stringResource(id = R.string.family_functions),
                icon = R.drawable.mandi, // TODO: Replace with family function icon
                modifier = Modifier
                    .weight(1f)
                    .height(150.sdp()),
                onClick = onFamilyFunctionClick
            )
        }
    }
}

@Composable
fun ServiceCard(
    title: String,
    @DrawableRes icon: Int,
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
                    painter = painterResource(id = icon),
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
            state = HomeState(selectedVillage = "Maharajganj"),
            onEvent = {},
            onNavigateToNotifications = {},
            onNavigateToConstruction = {},
            onNavigateToLabour = {},
            onNavigateToTransport = {},
            onNavigateToMandi = {},
            onNavigateToNews = {},
            onNavigateToHealth = {},
            onNavigateToFamilyFunction = {},
            onNavigateToLanguage = {},
            onNavigateToChangeVillage = {},
            onNavigateToAboutUs = {},
            onNavigateToPrivacyPolicy = {},
            onNavigateToTerms = {}
        )
    }
}
