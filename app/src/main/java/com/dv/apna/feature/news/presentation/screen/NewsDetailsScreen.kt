package com.dv.apna.feature.news.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import com.dv.apna.R
import com.dv.apna.core.theme.AapanGavTheme
import com.dv.apna.core.utils.sdp
import com.dv.apna.core.utils.ssp
import com.dv.apna.core.components.NewsDetailsSkeleton
import com.dv.apna.feature.news.presentation.state.NewsState

@Composable
fun NewsDetailsScreen(
    newsId: String,
    state: NewsState,
    onNavigateBack: () -> Unit,
) {
    // Try to find in news first, then in notices as a fallback
    val news = state.news.find { it.id == newsId } ?: state.notices.find { it.id == newsId }

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
            NewsTopBar(title = stringResource(id = R.string.news_details), onBackClick = onNavigateBack)

            if (state.isLoading) {
                NewsDetailsSkeleton()
            } else if (news != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.sdp())
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = news.title,
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
                            painter = painterResource(id = R.drawable.clock),
                            contentDescription = null,
                            modifier = Modifier.size(14.sdp()),
                            tint = Color(0xFF2CA074)
                        )
                        Text(
                            text = stringResource(id = R.string.published_label, formatDate(news.date)),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.ssp(),
                                fontWeight = FontWeight.Medium
                            ),
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(16.sdp()))

                    AsyncImage(
                        model = news.image,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.sdp())
                            .clip(RoundedCornerShape(15.sdp())),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.iv_dummy_banner),
                        error = painterResource(id = R.drawable.iv_dummy_banner)
                    )

                    Spacer(modifier = Modifier.height(20.sdp()))

                    Text(
                        text = news.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.ssp(),
                            lineHeight = 22.ssp()
                        ),
                        color = Color(0xFF4A4A4A)
                    )

                    Spacer(modifier = Modifier.height(100.sdp()))
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(id = R.string.no_records_found),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
    }
}
}

@Preview(showBackground = true)
@Composable
fun NewsDetailsScreenPreview() {
    AapanGavTheme {
        NewsDetailsScreen(
            newsId = "1",
            state = NewsState(),
            onNavigateBack = {}
        )
    }
}
