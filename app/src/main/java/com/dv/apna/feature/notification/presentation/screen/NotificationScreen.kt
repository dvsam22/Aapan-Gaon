package com.dv.apna.feature.notification.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import com.dv.apna.R
import com.dv.apna.core.components.AapanGavErrorScreen
import com.dv.apna.core.components.AapanGavEmptyData
import com.dv.apna.core.components.NotificationSkeleton
import com.dv.apna.core.theme.AapanGavTheme
import com.dv.apna.core.utils.sdp
import com.dv.apna.core.utils.ssp
import com.dv.apna.feature.notification.domain.model.NotificationModel
import com.dv.apna.feature.notification.presentation.effect.NotificationEffect
import com.dv.apna.feature.notification.presentation.event.NotificationEvent
import com.dv.apna.feature.notification.presentation.state.NotificationState
import kotlinx.coroutines.flow.collectLatest

@Composable
fun NotificationScreen(
    state: NotificationState,
    onEvent: (NotificationEvent) -> Unit,
    effect: kotlinx.coroutines.flow.Flow<NotificationEffect>,
    onNavigateToDetails: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    LaunchedEffect(Unit) {
        effect.collectLatest { effect ->
            when (effect) {
                is NotificationEffect.NavigateToDetails -> onNavigateToDetails(effect.notificationId)
                is NotificationEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(androidx.compose.ui.graphics.Brush.verticalGradient(listOf(com.dv.apna.core.theme.MintGradientStart, com.dv.apna.core.theme.MintGradientMiddle, com.dv.apna.core.theme.MintGradientEnd)))
    ) {
        val (bottomImage, mainContent, overlay) = createRefs()

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
            NotificationTopBar(
                onBackClick = { onEvent(NotificationEvent.BackClick) }
            )

            if (state.isLoading) {
                NotificationSkeleton()
            } else if (state.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    AapanGavErrorScreen(message = state.error.asString(), onRetry = { onEvent(NotificationEvent.Refresh) })
                }
            } else if (state.notifications.isEmpty()) {
                AapanGavEmptyData(message = stringResource(id = R.string.no_notifications))
            } else {
                val groupedNotifications = state.notifications.groupBy { it.category }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.sdp()),
                    verticalArrangement = Arrangement.spacedBy(8.sdp())
                ) {
                    groupedNotifications.forEach { (category, notifications) ->
                        item {
                            Text(
                                text = category, style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold, fontSize = 12.ssp()
                                ), color = Color.Black, modifier = Modifier.padding(bottom = 8.sdp())
                            )
                        }
                        items(notifications) { notification ->
                            NotificationCard(
                                notification = notification,
                                onClick = { onEvent(NotificationEvent.SelectNotification(notification.id)) })
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun NotificationTopBar(onBackClick: () -> Unit) {
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

        Text(
            text = stringResource(id = R.string.notification),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.ssp()
            ),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun NotificationCard(
    notification: NotificationModel, onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.sdp())
            .clickable { onClick() },
        shape = RoundedCornerShape(15.sdp()),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.sdp()),
        border = BorderStroke(1.sdp(), Color(0x662CA074))
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.sdp())
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.sdp())
                    .background(Color(0xFFEEF7F6), RoundedCornerShape(8.sdp()))
                    .border(1.sdp(), Color(0x6638C792), RoundedCornerShape(8.sdp())),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.notification_bing),
                    contentDescription = null,
                    modifier = Modifier.size(20.sdp()),
                    tint = Color(0xFF2CA074)
                )
            }

            Spacer(modifier = Modifier.width(12.sdp()))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.ssp()
                        ),
                        color = Color.Black,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = notification.time,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 10.ssp()
                        ),
                        color = Color.Black.copy(alpha = 0.4f),
                        modifier = Modifier.padding(start = 8.sdp())
                    )
                }

                Text(
                    text = notification.summary,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.ssp()
                    ),
                    color = Color.Black.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
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

@Preview(showBackground = true)
@Composable
fun NotificationScreenPreview() {
    AapanGavTheme {
        NotificationScreen(
            state = NotificationState(
            notifications = listOf(
                NotificationModel(
                    id = "1",
                    title = "Government increases MSP for Kharif crops",
                    summary = "The government has announced an....",
                    description = "",
                    time = "2 Hr ago",
                    date = "",
                    category = "Today"
                )
            )
        ),
            onEvent = {},
            effect = kotlinx.coroutines.flow.emptyFlow(),
            onNavigateToDetails = {},
            onNavigateBack = {})
    }
}
