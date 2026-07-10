package com.dv.apna.feature.splash.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.dv.apna.R
import com.dv.apna.core.theme.AapanGavTheme
import com.dv.apna.core.theme.Primary
import com.dv.apna.core.utils.sdp
import com.dv.apna.core.utils.ssp
import com.dv.apna.feature.splash.presentation.effect.SplashEffect
import com.dv.apna.feature.splash.presentation.viewmodel.SplashViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToLanguage: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is SplashEffect.NavigateToHome -> onNavigateToHome()
                is SplashEffect.NavigateToLanguage -> onNavigateToLanguage()
            }
        }
    }

    SplashContent()
}

@Composable
fun SplashContent() {
    val logoSize = 150.sdp()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Image(
            painter = painterResource(id = R.drawable.splash_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            val (logo, title) = createRefs()
            val topGuideline = createGuidelineFromTop(0.25f)

            Image(
                painter = painterResource(id = R.drawable.iv_splash_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(logoSize)
                    .constrainAs(logo) {
                        top.linkTo(topGuideline)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )

            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color(0xFF424242))) {
                        append("हर काम का साथी, ")
                    }
                    withStyle(style = SpanStyle(color = Primary)) {
                        append("अपना गाँव")
                    }
                },
                fontSize = 16.ssp(),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.constrainAs(title) {
                    top.linkTo(logo.bottom, margin = 10.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    AapanGavTheme {
        SplashContent()
    }
}
