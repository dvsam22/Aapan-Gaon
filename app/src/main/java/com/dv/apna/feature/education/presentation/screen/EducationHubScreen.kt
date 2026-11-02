package com.dv.apna.feature.education.presentation.screen

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.dv.apna.R
import com.dv.apna.core.ads.BannerAdView
import com.dv.apna.core.config.RemoteConfigManager
import com.dv.apna.core.theme.MintGradientEnd
import com.dv.apna.core.theme.MintGradientMiddle
import com.dv.apna.core.theme.MintGradientStart
import com.dv.apna.core.utils.sdp
import com.dv.apna.core.utils.ssp

@Composable
fun EducationHubScreen(
    remoteConfigManager: RemoteConfigManager,
    onNavigateBack: () -> Unit,
    onNavigateToDetails: (categoryId: String, categoryName: String) -> Unit
) {
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
            EducationTopBar(onBackClick = onNavigateBack)

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 16.sdp(), vertical = 8.sdp()),
                verticalArrangement = Arrangement.spacedBy(12.sdp())
            ) {
                item {
                    val catName = stringResource(id = R.string.schools_colleges)
                    EducationOptionCard(
                        title = catName,
                        subtitle = stringResource(id = R.string.schools_colleges_desc),
                        icon = painterResource(id = R.drawable.ic_school_building),
                        onClick = { onNavigateToDetails("schools", catName) }
                    )
                }
                item {
                    val catName = stringResource(id = R.string.teachers_tutors)
                    EducationOptionCard(
                        title = catName,
                        subtitle = stringResource(id = R.string.teachers_tutors_desc),
                        icon = painterResource(id = R.drawable.ic_teacher),
                        onClick = { onNavigateToDetails("teachers", catName) }
                    )
                }
                item {
                    val catName = stringResource(id = R.string.study_notes_materials)
                    EducationOptionCard(
                        title = catName,
                        subtitle = stringResource(id = R.string.study_notes_materials_desc),
                        icon = painterResource(id = R.drawable.ic_notes),
                        onClick = { onNavigateToDetails("notes", catName) }
                    )
                }
                item {
                    val catName = stringResource(id = R.string.scholarships_exams)
                    EducationOptionCard(
                        title = catName,
                        subtitle = stringResource(id = R.string.scholarships_exams_desc),
                        icon = painterResource(id = R.drawable.ic_scholarship),
                        onClick = { onNavigateToDetails("scholarships", catName) }
                    )
                }
                item {
                    val catName = stringResource(id = R.string.stationery_cyber_cafe)
                    EducationOptionCard(
                        title = catName,
                        subtitle = stringResource(id = R.string.stationery_cyber_cafe_desc),
                        icon = painterResource(id = R.drawable.ic_stationery),
                        onClick = { onNavigateToDetails("stationery", catName) }
                    )
                }
            }

            BannerAdView(remoteConfigManager = remoteConfigManager)
        }
    }
}

@Composable
fun EducationTopBar(
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
            text = stringResource(id = R.string.education_school_hub),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.ssp()
            ),
            color = Color(0xFF1E293B)
        )
    }
}

@Composable
fun EducationOptionCard(
    title: String,
    subtitle: String,
    icon: Painter,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() },
        shape = RoundedCornerShape(14.sdp()),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.sdp()),
        border = BorderStroke(1.sdp(), Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.sdp()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(46.sdp()),
                shape = RoundedCornerShape(12.sdp()),
                color = Color(0xFFEFFAF6),
                border = BorderStroke(1.sdp(), Color(0xFF38C792).copy(alpha = 0.35f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = icon,
                        contentDescription = null,
                        modifier = Modifier.size(26.sdp()),
                        tint = Color(0xFF2CA074)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.sdp()))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.ssp()
                    ),
                    color = Color(0xFF1E293B)
                )

                Spacer(modifier = Modifier.height(2.sdp()))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.ssp()
                    ),
                    color = Color(0xFF64748B)
                )
            }

            Icon(
                painter = painterResource(id = R.drawable.arrow_circle_right),
                contentDescription = null,
                modifier = Modifier.size(20.sdp()),
                tint = Color(0xFF38C792)
            )
        }
    }
}
