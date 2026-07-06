package com.dv.apna.feature.notification.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
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
                .navigationBarsPadding()
                .constrainAs(mainContent) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            NotificationTopBar(
                onBackClick = onNavigateBack
            )

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
                    contentDescription = "Back",
                    modifier = Modifier.size(20.sdp()),
                    tint = Color.Black
                )
            }
        }

        Text(
            text = "Notification",
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
            .clickable { onClick() },
        shape = RoundedCornerShape(15.sdp()),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.sdp()),
        border = BorderStroke(1.sdp(), Color(0x662CA074))
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.sdp(), vertical = 10.sdp())
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.sdp())
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

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = notification.title, style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium,
                            lineHeight = 16.ssp(),
                            fontSize = 12.ssp()

                        ), color = Color.Black, modifier = Modifier.weight(1f), maxLines = 2
                    )

                    Text(
                        text = notification.time,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 10.ssp()
                        ),
                        color = Color.Black.copy(alpha = 0.5f),
                        modifier = Modifier.padding(start = 8.sdp())
                    )
                }

                Spacer(modifier = Modifier.height(2.sdp()))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.summary,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 10.ssp()
                        ),
                        color = Color.Black.copy(alpha = 0.7f),
                        modifier = Modifier.weight(1f),
                        maxLines = 1
                    )

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
