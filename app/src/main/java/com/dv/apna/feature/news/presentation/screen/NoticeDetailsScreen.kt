package com.dv.apna.feature.news.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
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
import com.dv.apna.feature.news.presentation.state.NewsState

@Composable
fun NoticeDetailsScreen(
    noticeId: String,
    state: NewsState,
    onNavigateBack: () -> Unit,
) {
    val notice = state.notices.find { it.id == noticeId } ?: return

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
            NewsTopBar(title = "Notice Details", onBackClick = onNavigateBack)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.sdp())
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = notice.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.ssp(),
                        lineHeight = 24.ssp()
                    ),
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(12.sdp()))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.sdp())
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarMonth,
                        contentDescription = null,
                        modifier = Modifier.size(14.sdp()),
                        tint = Color(0xFF2CA074)
                    )
                    Text(
                        text = "Date: ${notice.date}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.ssp(),
                            fontWeight = FontWeight.Medium
                        ),
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(20.sdp()))

                Text(
                    text = notice.description.ifEmpty { notice.summary },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.ssp(),
                        lineHeight = 22.ssp()
                    ),
                    color = Color(0xFF4A4A4A)
                )
                
                Spacer(modifier = Modifier.height(100.sdp()))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NoticeDetailsScreenPreview() {
    AapanGavTheme {
        NoticeDetailsScreen(
            noticeId = "1",
            state = NewsState(),
            onNavigateBack = {}
        )
    }
}
