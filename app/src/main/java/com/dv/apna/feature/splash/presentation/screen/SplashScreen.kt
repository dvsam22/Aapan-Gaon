package com.dv.apna.feature.splash.presentation.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.dv.apna.R
import com.dv.apna.core.theme.AapanGavTheme
import com.dv.apna.core.theme.NotoSansHindi
import com.dv.apna.core.theme.Primary
import com.dv.apna.core.utils.sdp
import com.dv.apna.core.utils.ssp
import com.dv.apna.feature.splash.presentation.effect.SplashEffect
import com.dv.apna.feature.splash.presentation.viewmodel.SplashViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToLanguage: () -> Unit,
    onNavigateToDetails: (id: String, type: String?) -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is SplashEffect.NavigateToHome -> onNavigateToHome()
                is SplashEffect.NavigateToLanguage -> onNavigateToLanguage()
                is SplashEffect.NavigateToNotificationDetails -> onNavigateToDetails(effect.id, effect.type)
            }
        }
    }

    SplashContent()
}

@Composable
fun SplashContent() {
    val logoSize = 220.sdp()

    // Animation states
    val alpha = remember { Animatable(0f) }
    val scale = remember { Animatable(0.8f) }
    val textAlpha = remember { Animatable(0f) }
    val textOffsetY = remember { Animatable(20f) }

    LaunchedEffect(Unit) {
        // Logo animation
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
            )
        }
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
            )
        }

        // Text and other elements animation starts slightly later
        delay(400)
        launch {
            textAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 800)
            )
        }
        launch {
            textOffsetY.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
            )
        }
    }

    val bgBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFBCEFD0),
            Color(0xFFEBF8F2),
            Color(0xFFFFFFFF)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgBrush)
    ) {
        // Decorative background elements
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color(0xFF38C792).copy(alpha = 0.05f),
                radius = 350.dp.toPx(),
                center = Offset(size.width * 0.1f, size.height * 0.15f)
            )
            drawCircle(
                color = Color(0xFF38C792).copy(alpha = 0.03f),
                radius = 250.dp.toPx(),
                center = Offset(size.width * 0.9f, size.height * 0.75f)
            )
        }

        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            val (logo, appName, tagline, loader, footer) = createRefs()
            val topGuideline = createGuidelineFromTop(0.20f)

            Image(
                painter = painterResource(id = R.drawable.trasparent_logo),
                contentDescription = "Splash Logo",
                modifier = Modifier
                    .size(logoSize)
                    .graphicsLayer(
                        alpha = alpha.value,
                        scaleX = scale.value,
                        scaleY = scale.value
                    )
                    .constrainAs(logo) {
                        top.linkTo(topGuideline)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )

            Text(
                text = stringResource(id = R.string.splash_text_part2),
                fontSize = 28.ssp(),
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF165D47),
                fontFamily = NotoSansHindi,
                modifier = Modifier
                    .graphicsLayer(
                        alpha = textAlpha.value,
                        translationY = textOffsetY.value
                    )
                    .constrainAs(appName) {
                        top.linkTo(logo.bottom, margin = 16.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )

            Text(
                text = stringResource(id = R.string.splash_text_part1),
                fontSize = 16.ssp(),
                fontWeight = FontWeight.Medium,
                color = Color(0xFF387764),
                fontFamily = NotoSansHindi,
                letterSpacing = 0.5.sp,
                modifier = Modifier
                    .graphicsLayer(
                        alpha = textAlpha.value,
                        translationY = textOffsetY.value
                    )
                    .constrainAs(tagline) {
                        top.linkTo(appName.bottom, margin = 8.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )

            CircularProgressIndicator(
                modifier = Modifier
                    .size(38.dp)
                    .graphicsLayer(alpha = textAlpha.value)
                    .constrainAs(loader) {
                        bottom.linkTo(footer.top, margin = 32.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                color = Primary,
                strokeWidth = 3.dp
            )

            Text(
                text = "© Apna Gaon",
                fontSize = 12.ssp(),
                fontWeight = FontWeight.Normal,
                color = Color(0xFF88A096),
                modifier = Modifier
                    .graphicsLayer(alpha = textAlpha.value)
                    .constrainAs(footer) {
                        bottom.linkTo(parent.bottom, margin = 24.dp)
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

