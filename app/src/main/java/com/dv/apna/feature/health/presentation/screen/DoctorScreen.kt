package com.dv.apna.feature.health.presentation.screen

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
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.dv.apna.R
import com.dv.apna.core.components.AapanGavErrorScreen
import com.dv.apna.core.components.AapanGavEmptyData
import com.dv.apna.core.components.HealthSkeleton
import com.dv.apna.core.utils.sdp
import com.dv.apna.core.utils.ssp
import com.dv.apna.feature.health.domain.model.DoctorModel
import com.dv.apna.feature.health.presentation.effect.HealthEffect
import com.dv.apna.feature.health.presentation.event.HealthEvent
import com.dv.apna.feature.health.presentation.state.HealthState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun DoctorScreen(
    state: HealthState,
    onEvent: (HealthEvent) -> Unit,
    effect: Flow<HealthEffect>,
    remoteConfigManager: com.dv.apna.core.config.RemoteConfigManager,
    onNavigateBack: () -> Unit,
    onDialPhone: (String) -> Unit
) {
    LaunchedEffect(Unit) {
        effect.collectLatest { effect ->
            when (effect) {
                HealthEffect.NavigateBack -> onNavigateBack()
                is HealthEffect.DialPhone -> onDialPhone(effect.phone)
                else -> {}
            }
        }
    }

    val searchQueryState = androidx.compose.runtime.saveable.rememberSaveable { androidx.compose.runtime.mutableStateOf("") }
    val query = searchQueryState.value
    val filteredDoctors = remember(state.doctors, query) {
        if (query.isBlank()) {
            state.doctors
        } else {
            state.doctors.filter { doc ->
                doc.name.contains(query, ignoreCase = true) ||
                doc.specialization.contains(query, ignoreCase = true) ||
                doc.address.contains(query, ignoreCase = true) ||
                doc.availability.contains(query, ignoreCase = true) ||
                doc.phone.contains(query, ignoreCase = true)
            }
        }
    }

    val nativeAd = com.dv.apna.core.ads.rememberNativeAd(
        adUnitId = remoteConfigManager.getNativeAdUnitId(com.dv.apna.core.config.ServiceAdCategory.HEALTH),
        isEnabled = remoteConfigManager.isNativeAdsEnabled()
    )

    val adPositions = remember(filteredDoctors.size) {
        val positions = mutableSetOf<Int>()
        if (filteredDoctors.size > 3) {
            var current = kotlin.random.Random.nextInt(4, 7)
            while (current < filteredDoctors.size) {
                positions.add(current)
                current += kotlin.random.Random.nextInt(6, 11)
            }
        } else if (filteredDoctors.isNotEmpty()) {
            positions.add(filteredDoctors.lastIndex)
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
            DoctorTopBar(onBackClick = { onEvent(HealthEvent.BackClick) })
            com.dv.apna.core.components.AapanGavSearchBar(
                query = query,
                onQueryChange = { searchQueryState.value = it }
            )

            if (state.isLoading) {
                HealthSkeleton()
            } else {
                if (filteredDoctors.isEmpty() && state.error == null) {
                    AapanGavEmptyData(
                        message = if (query.isNotEmpty()) stringResource(id = R.string.no_results_found) else stringResource(id = R.string.no_doctors_found)
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
                            items = filteredDoctors,
                            key = { index, doctor -> if (doctor.id.isNotBlank()) doctor.id else "${doctor.name}_$index" }
                        ) { index, doctor ->
                            DoctorItemCard(doctor = doctor, onCallClick = { onEvent(HealthEvent.CallClick(doctor.phone)) })

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
fun DoctorTopBar(onBackClick: () -> Unit) {
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
            text = stringResource(id = R.string.doctors),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.ssp()
            ),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun DoctorItemCard(
    doctor: DoctorModel,
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
                            painter = painterResource(id = R.drawable.doctor),
                            contentDescription = null,
                            modifier = Modifier.size(32.sdp()),
                            tint = Color(0xFF38C792)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.sdp()))

                Column {
                    Text(
                        text = doctor.name,
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
                            text = doctor.address,
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
                    painter = painterResource(id = R.drawable.specialisation),
                    contentDescription = null,
                    modifier = Modifier.size(14.sdp()),
                    tint = Color(0xFF2CA074)
                )
                Spacer(modifier = Modifier.width(8.sdp()))
                Text(
                    text = stringResource(id = R.string.specialization_label),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.ssp()
                    ),
                    color = Color.Black
                )
                Spacer(modifier = Modifier.width(4.sdp()))
                Text(
                    text = doctor.specialization,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.ssp(),
                        color = Color.Black.copy(alpha = 0.7f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.sdp()))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.clock),
                    contentDescription = null,
                    modifier = Modifier.size(14.sdp()),
                    tint = Color(0xFF2CA074)
                )
                Spacer(modifier = Modifier.width(8.sdp()))
                Text(
                    text = stringResource(id = R.string.availability_label),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.ssp()
                    ),
                    color = Color.Black
                )
                Spacer(modifier = Modifier.width(4.sdp()))
                Text(
                    text = doctor.availability,
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

@androidx.compose.runtime.Composable
@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
fun DoctorScreenPreview() {
    com.dv.apna.core.theme.AapanGavTheme {
        DoctorScreen(
            state = com.dv.apna.feature.health.presentation.state.HealthState(
                doctors = listOf(
                    com.dv.apna.feature.health.domain.model.DoctorModel("1", "Dr. Anil Sharma", "Rampur Village", "General Physician", "09:30AM - 04:00PM", "1234567890")
                )
            ),
            onEvent = {},
            effect = kotlinx.coroutines.flow.emptyFlow(),
            remoteConfigManager = com.dv.apna.core.config.RemoteConfigManager(),
            onNavigateBack = {},
            onDialPhone = {}
        )
    }
}
