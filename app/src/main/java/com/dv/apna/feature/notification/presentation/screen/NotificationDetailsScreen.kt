package com.dv.apna.feature.notification.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.dv.apna.feature.notification.presentation.state.NotificationState

@Composable
fun NotificationDetailsScreen(
    notificationId: String, state: NotificationState, onNavigateBack: () -> Unit
) {
    val notification = state.notifications.find { it.id == notificationId } ?: return

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
                }) {
            NotificationTopBar(
                onBackClick = onNavigateBack
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.sdp())
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = notification.title, style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold, fontSize = 18.ssp()
                    ), color = Color.Black
                )

                Spacer(modifier = Modifier.height(12.sdp()))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.sdp())
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.clock),
                        contentDescription = null,
                        modifier = Modifier.size(16.sdp()),
                        tint = Color(0xFF38C792)
                    )
                    Text(
                        text = "Published: ${notification.date}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.ssp(), fontWeight = FontWeight.Medium
                        ),
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(20.sdp()))

                Text(
                    text = notification.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.ssp(), lineHeight = 22.ssp()
                    ),
                    color = Color.DarkGray
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationDetailsPreview() {
    AapanGavTheme {
        NotificationDetailsScreen(
            notificationId = "1", state = NotificationState(
                notifications = listOf(
                    NotificationModel(
                        id = "1",
                        title = "Government Increases MSP for Kharif Crops",
                        summary = "",
                        description = "The Government of India has approved an increase in the Minimum Support Price (MSP) for major Kharif crops for the upcoming sowing season. The revised MSP aims to provide better income to farmers while encouraging the cultivation of essential food grains and oilseeds.",
                        time = "",
                        date = "Today, 01:30PM",
                        category = ""
                    )
                )
            ), onNavigateBack = {})
    }
}
