package com.dv.apna.feature.health.presentation.screen

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.dv.apna.R
import com.dv.apna.core.theme.AapanGavTheme
import com.dv.apna.core.utils.sdp
import com.dv.apna.core.utils.ssp
import com.dv.apna.feature.health.presentation.effect.HealthEffect
import com.dv.apna.feature.health.presentation.event.HealthEvent
import com.dv.apna.feature.health.presentation.state.HealthState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HealthHubScreen(
    state: HealthState,
    onEvent: (HealthEvent) -> Unit,
    effect: Flow<HealthEffect>,
    onNavigateBack: () -> Unit,
    onNavigateToDoctors: () -> Unit,
    onNavigateToHospitals: () -> Unit,
    onNavigateToPharmacy: () -> Unit,
    onDialPhone: (String) -> Unit
) {
    LaunchedEffect(Unit) {
        effect.collectLatest { effect ->
            when (effect) {
                HealthEffect.NavigateBack -> onNavigateBack()
                is HealthEffect.DialPhone -> onDialPhone(effect.phone)
                HealthEffect.NavigateToDoctors -> onNavigateToDoctors()
                HealthEffect.NavigateToHospitals -> onNavigateToHospitals()
                HealthEffect.NavigateToPharmacy -> onNavigateToPharmacy()
            }
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
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
            HealthTopBar(onBackClick = { onEvent(HealthEvent.BackClick) })

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.sdp(), vertical = 8.sdp()),
                verticalArrangement = Arrangement.spacedBy(16.sdp())
            ) {
                // Emergency Cards
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.sdp())
                    ) {
                        EmergencyCard(
                            title = "Ambulance",
                            icon = R.drawable.ambulance,
                            onClick = { onEvent(HealthEvent.CallAmbulance("108")) },
                            modifier = Modifier.weight(1f)
                        )
                        EmergencyCard(
                            title = "Police",
                            icon = R.drawable.police,
                            onClick = { onEvent(HealthEvent.CallPolice("100")) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Service List
                item {
                    Spacer(modifier = Modifier.height(8.sdp()))
                    HealthOptionCard(
                        title = "Doctors",
                        icon = painterResource(id = R.drawable.doctor),
                        onClick = { onEvent(HealthEvent.DoctorsClick) }
                    )
                }
                item {
                    HealthOptionCard(
                        title = "Hospitals",
                        icon = painterResource(id = R.drawable.hospital),
                        onClick = { onEvent(HealthEvent.HospitalsClick) }
                    )
                }
                item {
                    HealthOptionCard(
                        title = "Pharmacy",
                        icon = painterResource(id = R.drawable.pharmecy),
                        onClick = { onEvent(HealthEvent.PharmacyClick) }
                    )
                }
            }
        }
    }
}

@Composable
fun HealthTopBar(onBackClick: () -> Unit) {
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
            text = "Health & Emergency",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.ssp()
            ),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun EmergencyCard(
    title: String,
    icon: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.wrapContentHeight(),
        shape = RoundedCornerShape(15.sdp()),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.sdp())
    ) {
        Box(
            modifier = Modifier
                .padding(4.sdp())
                .background(Color(0xFFEFFAF6), RoundedCornerShape(12.sdp()))
                .padding(vertical = 16.sdp()),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier.size(28.sdp()),
                    tint = Color(0xFF2CA074)
                )
                Spacer(modifier = Modifier.height(8.sdp()))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.ssp()
                    ),
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(12.sdp()))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.sdp())
                ) {
                    Text(
                        text = "<<",
                        color = Color(0xFF38C792).copy(alpha = 0.4f),
                        fontSize = 14.ssp(),
                        fontWeight = FontWeight.Light
                    )

                    Button(
                        onClick = onClick,
                        modifier = Modifier
                            .width(80.sdp())
                            .height(26.sdp()),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38C792)),
                        shape = RoundedCornerShape(20.sdp()),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "Call Now",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.ssp(),
                                fontWeight = FontWeight.Medium
                            ),
                            color = Color.White
                        )
                    }

                    Text(
                        text = ">>",
                        color = Color(0xFF38C792).copy(alpha = 0.4f),
                        fontSize = 14.ssp(),
                        fontWeight = FontWeight.Light
                    )
                }
            }
        }
    }
}

@Composable
fun HealthOptionCard(
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
fun HealthHubScreenPreview() {
    AapanGavTheme {
        HealthHubScreen(
            state = HealthState(),
            onEvent = {},
            effect = kotlinx.coroutines.flow.emptyFlow(),
            onNavigateBack = {},
            onNavigateToDoctors = {},
            onNavigateToHospitals = {},
            onNavigateToPharmacy = {},
            onDialPhone = {}
        )
    }
}
