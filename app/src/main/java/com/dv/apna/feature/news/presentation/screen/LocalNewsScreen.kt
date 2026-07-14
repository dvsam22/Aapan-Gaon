package com.dv.apna.feature.news.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import com.dv.apna.R
import com.dv.apna.core.theme.AapanGavTheme
import com.dv.apna.core.utils.sdp
import com.dv.apna.core.utils.ssp
import com.dv.apna.core.components.NewsSkeleton
import com.dv.apna.core.components.AapanGavEmptyData
import com.dv.apna.feature.news.domain.model.NewsModel
import com.dv.apna.feature.news.presentation.effect.NewsEffect
import com.dv.apna.feature.news.presentation.event.NewsEvent
import com.dv.apna.feature.news.presentation.state.NewsState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LocalNewsScreen(
    state: NewsState,
    onEvent: (NewsEvent) -> Unit,
    effect: Flow<NewsEffect>,
    onNavigateBack: () -> Unit,
    onNavigateToNewsDetails: (String) -> Unit,
    onNavigateToNoticeDetails: (String) -> Unit
) {
    LaunchedEffect(Unit) {
        effect.collectLatest { effect ->
            when (effect) {
                NewsEffect.NavigateBack -> onNavigateBack()
                is NewsEffect.NavigateToNewsDetails -> onNavigateToNewsDetails(effect.id)
                is NewsEffect.NavigateToNoticeDetails -> onNavigateToNoticeDetails(effect.id)
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
            NewsTopBar(title = stringResource(id = R.string.breaking_news), onBackClick = { onEvent(NewsEvent.BackClick) })

            if (state.isLoading) {
                NewsSkeleton()
            } else if (state.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.error.asString(), color = Color.Red)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.sdp())
                ) {
                    // Breaking News Section
                    if (state.news.isNotEmpty()) {
                        items(state.news) { news ->
                            NewsItemCard(
                                news = news,
                                onClick = { onEvent(NewsEvent.NewsClick(news.id)) }
                            )
                        }
                    }

                    if (state.news.isEmpty() && !state.isLoading) {
                        item {
                            AapanGavEmptyData(
                                modifier = Modifier.fillParentMaxSize(),
                                message = stringResource(id = R.string.no_news_available)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NewsTopBar(title: String, onBackClick: () -> Unit) {
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
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.ssp()
            ),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun NewsItemCard(
    news: NewsModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.sdp(), vertical = 6.sdp())
            .clickable { onClick() },
        shape = RoundedCornerShape(15.sdp()),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.sdp(), Color(0xFF2CA074).copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.sdp())
    ) {
        Row(
            modifier = Modifier
                .padding(12.sdp())
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            AsyncImage(
                model = news.image,
                contentDescription = null,
                modifier = Modifier
                    .size(80.sdp())
                    .clip(RoundedCornerShape(10.sdp())),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.iv_dummy_banner),
                error = painterResource(id = R.drawable.iv_dummy_banner)
            )

            Spacer(modifier = Modifier.width(12.sdp()))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = news.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.ssp(),
                        lineHeight = 16.ssp()
                    ),
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.sdp()))

                Text(
                    text = news.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 10.ssp(),
                        color = Color.Gray
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.sdp()))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.clock),
                        contentDescription = null,
                        modifier = Modifier.size(14.sdp()),
                        tint = Color(0xFF2CA074)
                    )
                    Spacer(modifier = Modifier.width(4.sdp()))
                    Text(
                        text = formatDate(news.date),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 10.ssp(),
                            color = Color.Gray
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun NoticeItemCard(
    notice: NewsModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.sdp(), vertical = 6.sdp())
            .clickable { onClick() },
        shape = RoundedCornerShape(15.sdp()),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.sdp(), Color(0xFF2CA074).copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.sdp())
    ) {
        Row(
            modifier = Modifier
                .padding(12.sdp())
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.sdp())
                    .background(Color(0xFFEFFAF6), RoundedCornerShape(10.sdp())),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.receipt),
                    contentDescription = null,
                    modifier = Modifier.size(28.sdp()),
                    tint = Color(0xFF2CA074)
                )
            }

            Spacer(modifier = Modifier.width(12.sdp()))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notice.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.ssp()
                    ),
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.sdp()))

                Text(
                    text = notice.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 10.ssp(),
                        color = Color.Gray
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.sdp()))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarMonth,
                        contentDescription = null,
                        modifier = Modifier.size(14.sdp()),
                        tint = Color(0xFF2CA074)
                    )
                    Spacer(modifier = Modifier.width(4.sdp()))
                    Text(
                        text = formatDate(notice.date),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 10.ssp(),
                            color = Color.Gray
                        )
                    )
                }
            }
        }
    }
}

fun formatDate(timestamp: Long): String {
    if (timestamp == 0L) return ""
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

@Preview(showBackground = true)
@Composable
fun LocalNewsScreenPreview() {
    AapanGavTheme {
        LocalNewsScreen(
            state = NewsState(),
            onEvent = {},
            effect = kotlinx.coroutines.flow.emptyFlow(),
            onNavigateBack = {},
            onNavigateToNewsDetails = {},
            onNavigateToNoticeDetails = {}
        )
    }
}
