package com.dv.apna.feature.education.presentation.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.constraintlayout.compose.ConstraintLayout
import com.dv.apna.R
import com.dv.apna.core.ads.BannerAdView
import com.dv.apna.core.components.AapanGavEmptyData
import com.dv.apna.core.components.AapanGavErrorScreen
import com.dv.apna.core.components.AapanGavSearchBar
import com.dv.apna.core.components.LabourSkeleton
import com.dv.apna.core.config.RemoteConfigManager
import com.dv.apna.core.theme.MintGradientEnd
import com.dv.apna.core.theme.MintGradientMiddle
import com.dv.apna.core.theme.MintGradientStart
import com.dv.apna.core.utils.sdp
import com.dv.apna.core.utils.ssp
import com.dv.apna.feature.education.domain.model.EducationItemModel
import com.dv.apna.feature.education.presentation.effect.EducationEffect
import com.dv.apna.feature.education.presentation.event.EducationEvent
import com.dv.apna.feature.education.presentation.state.EducationState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun EducationDetailsScreen(
    state: EducationState,
    onEvent: (EducationEvent) -> Unit,
    effect: Flow<EducationEffect>,
    remoteConfigManager: RemoteConfigManager,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        effect.collectLatest { eff ->
            when (eff) {
                EducationEffect.NavigateBack -> onNavigateBack()
                is EducationEffect.DialPhone -> {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${eff.contact}"))
                    context.startActivity(intent)
                }
                is EducationEffect.OpenWhatsApp -> {
                    try {
                        val url = "https://api.whatsapp.com/send?phone=91${eff.phone}"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    } catch (_: Exception) {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${eff.phone}"))
                        context.startActivity(intent)
                    }
                }
                is EducationEffect.OpenBrowser -> {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(eff.url))
                        context.startActivity(intent)
                    } catch (_: Exception) {}
                }
            }
        }
    }

    val searchQueryState = rememberSaveable { mutableStateOf("") }
    val query = searchQueryState.value
    val filteredItems = remember(state.items, query) {
        if (query.isBlank()) {
            state.items
        } else {
            state.items.filter { item ->
                item.name.contains(query, ignoreCase = true) ||
                item.roleOrType.contains(query, ignoreCase = true) ||
                item.location.contains(query, ignoreCase = true) ||
                item.description.contains(query, ignoreCase = true) ||
                item.contact.contains(query, ignoreCase = true)
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
                .constrainAs(mainContent) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            EducationDetailsTopBar(
                title = state.categoryTitle.ifBlank { stringResource(id = R.string.education_school_hub) },
                onBackClick = { onEvent(EducationEvent.BackClicked) }
            )

            AapanGavSearchBar(
                query = query,
                onQueryChange = { searchQueryState.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.sdp(), vertical = 6.sdp())
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when {
                    state.isLoading -> {
                        LabourSkeleton()
                    }
                    state.error != null -> {
                        AapanGavErrorScreen(
                            message = state.error.asString(),
                            onRetry = { onEvent(EducationEvent.Refresh) }
                        )
                    }
                    filteredItems.isEmpty() -> {
                        AapanGavEmptyData(
                            message = stringResource(id = R.string.no_education_data)
                        )
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.sdp(), vertical = 8.sdp()),
                            verticalArrangement = Arrangement.spacedBy(10.sdp())
                        ) {
                            items(filteredItems, key = { it.id }) { item ->
                                EducationItemCard(
                                    item = item,
                                    onCall = { onEvent(EducationEvent.CallClicked(item.contact)) },
                                    onWhatsApp = { onEvent(EducationEvent.WhatsAppClicked(item.whatsapp)) },
                                    onOpenLink = { if (item.fileUrl.isNotBlank()) onEvent(EducationEvent.OpenLink(item.fileUrl)) }
                                )
                            }
                        }
                    }
                }
            }

            BannerAdView(remoteConfigManager = remoteConfigManager)
        }
    }
}

@Composable
fun EducationDetailsTopBar(
    title: String,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.sdp(), vertical = 12.sdp()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(36.sdp()),
            shape = RoundedCornerShape(10.sdp()),
            color = Color.White,
            border = BorderStroke(1.sdp(), Color(0xFFD9D9D9)),
            onClick = onBackClick
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_left),
                    contentDescription = stringResource(id = R.string.back),
                    modifier = Modifier.size(16.sdp()),
                    tint = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.width(12.sdp()))

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 17.ssp()
            ),
            color = Color(0xFF1E293B),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun EducationItemCard(
    item: EducationItemModel,
    onCall: () -> Unit,
    onWhatsApp: () -> Unit,
    onOpenLink: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.sdp(),
                shape = RoundedCornerShape(14.sdp()),
                clip = false,
                ambientColor = Color.Black.copy(alpha = 0.2f),
                spotColor = Color.Black.copy(alpha = 0.2f)
            ),
        shape = RoundedCornerShape(14.sdp()),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.sdp(), Color(0xFF2CA074).copy(alpha = 0.25f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.sdp())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Surface(
                    modifier = Modifier.size(46.sdp()),
                    shape = RoundedCornerShape(12.sdp()),
                    color = Color(0xFFEFFAF6),
                    border = BorderStroke(1.sdp(), Color(0xFF38C792).copy(alpha = 0.35f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        val iconRes = when (item.categoryId) {
                            "schools" -> R.drawable.ic_school_building
                            "teachers" -> R.drawable.ic_teacher
                            "notes" -> R.drawable.ic_notes
                            "scholarships" -> R.drawable.ic_scholarship
                            else -> R.drawable.ic_stationery
                        }
                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = null,
                            modifier = Modifier.size(24.sdp()),
                            tint = Color(0xFF2CA074)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.sdp()))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.ssp()
                        ),
                        color = Color(0xFF0F172A)
                    )

                    if (item.roleOrType.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.sdp()))
                        Surface(
                            shape = RoundedCornerShape(4.sdp()),
                            color = Color(0xFFE8F5E9)
                        ) {
                            Text(
                                text = item.roleOrType,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 10.ssp(),
                                    color = Color(0xFF1B5E20)
                                ),
                                modifier = Modifier.padding(horizontal = 6.sdp(), vertical = 2.sdp())
                            )
                        }
                    }

                    if (item.location.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.sdp()))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.iv_location),
                                contentDescription = null,
                                modifier = Modifier.size(12.sdp()),
                                tint = Color(0xFF2CA074)
                            )
                            Spacer(modifier = Modifier.width(4.sdp()))
                            Text(
                                text = item.location,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.ssp(),
                                    color = Color(0xFF64748B)
                                )
                            )
                        }
                    }
                }
            }

            if (item.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.sdp()))
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.ssp(),
                        color = Color(0xFF334155),
                        lineHeight = 16.ssp()
                    )
                )
            }

            if (item.extraInfo.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.sdp()))
                Surface(
                    shape = RoundedCornerShape(6.sdp()),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.sdp(), Color(0xFFE2E8F0))
                ) {
                    Text(
                        text = item.extraInfo,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.ssp(),
                            color = Color(0xFF475569)
                        ),
                        modifier = Modifier.padding(horizontal = 8.sdp(), vertical = 4.sdp())
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.sdp()))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.sdp())
            ) {
                if (item.contact.isNotBlank()) {
                    Button(
                        onClick = onCall,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.sdp()),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38C792)),
                        contentPadding = PaddingValues(vertical = 8.sdp())
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.iv_call),
                            contentDescription = null,
                            modifier = Modifier.size(16.sdp()),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.sdp()))
                        Text(
                            text = stringResource(id = R.string.call_now),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.ssp()
                            ),
                            color = Color.White
                        )
                    }
                }

                if (item.whatsapp.isNotBlank()) {
                    OutlinedButton(
                        onClick = onWhatsApp,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.sdp()),
                        border = BorderStroke(1.sdp(), Color(0xFF25D366)),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                        contentPadding = PaddingValues(vertical = 8.sdp())
                    ) {
                        Text(
                            text = "WhatsApp 💬",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.ssp()
                            ),
                            color = Color(0xFF1E7E34)
                        )
                    }
                }

                if (item.fileUrl.isNotBlank()) {
                    Button(
                        onClick = onOpenLink,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.sdp()),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                        contentPadding = PaddingValues(vertical = 8.sdp())
                    ) {
                        Text(
                            text = "वेबसाइट / लिंक 🔗",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.ssp()
                            ),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
