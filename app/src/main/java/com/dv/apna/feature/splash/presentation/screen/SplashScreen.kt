package com.dv.apna.feature.splash.presentation.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.dv.apna.BuildConfig
import com.dv.apna.R
import com.dv.apna.core.theme.AapanGavTheme
import com.dv.apna.core.theme.NotoSansHindi
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
    val logoSize = 250.sdp()

    // Animation states
    val alpha = remember { Animatable(0f) }
    val scale = remember { Animatable(0.75f) }
    val textAlpha = remember { Animatable(0f) }
    val textOffsetY = remember { Animatable(24f) }

    // Breathing glow effect behind logo
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.50f,
        targetValue = 0.20f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    LaunchedEffect(Unit) {
        // Logo animation with smooth spring entry
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing)
            )
        }
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing)
            )
        }

        // Text, pills and footer animate smoothly shortly after
        delay(350)
        launch {
            textAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 750)
            )
        }
        launch {
            textOffsetY.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 750, easing = FastOutSlowInEasing)
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
        // Subtle ambient circles
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color(0xFF38C792).copy(alpha = 0.06f),
                radius = 350.dp.toPx(),
                center = Offset(size.width * 0.1f, size.height * 0.15f)
            )
            drawCircle(
                color = Color(0xFF38C792).copy(alpha = 0.04f),
                radius = 250.dp.toPx(),
                center = Offset(size.width * 0.9f, size.height * 0.75f)
            )
        }

        val appNameMargin = 4.sdp()
        val taglineMargin = 4.sdp()
        val featurePillsMargin = 12.sdp()
        val loaderMargin = 16.sdp()
        val footerMargin = 16.sdp()

        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            val (bottomImage, aura, logo, appName, tagline, featurePills, loader, footer) = createRefs()
            val topGuideline = createGuidelineFromTop(0.08f)

            // Bottom Village Silhouette
            Image(
                painter = painterResource(id = R.drawable.iv_bottomview),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(0.55f)
                    .constrainAs(bottomImage) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                contentScale = ContentScale.FillWidth
            )

            // Glowing Breathing Aura behind logo
            Box(
                modifier = Modifier
                    .size(logoSize * 1.10f)
                    .graphicsLayer(
                        scaleX = pulseScale,
                        scaleY = pulseScale,
                        alpha = alpha.value * pulseAlpha
                    )
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF38C792).copy(alpha = 0.55f),
                                Color(0xFF86EFAC).copy(alpha = 0.25f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
                    .constrainAs(aura) {
                        top.linkTo(logo.top)
                        bottom.linkTo(logo.bottom)
                        start.linkTo(logo.start)
                        end.linkTo(logo.end)
                    }
            )

            // App Logo
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

            // App Name: "अपना गाँव"
            Text(
                text = stringResource(id = R.string.splash_text_part2),
                fontSize = 30.ssp(),
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF165D47),
                fontFamily = NotoSansHindi,
                modifier = Modifier
                    .graphicsLayer(
                        alpha = textAlpha.value,
                        translationY = textOffsetY.value
                    )
                    .constrainAs(appName) {
                        top.linkTo(logo.bottom, margin = appNameMargin)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )

            // Tagline: "अपने गाँव का अपना ऐप"
            Text(
                text = stringResource(id = R.string.splash_text_part1),
                fontSize = 15.ssp(),
                fontWeight = FontWeight.Medium,
                color = Color(0xFF2E7D63),
                fontFamily = NotoSansHindi,
                letterSpacing = 0.5.sp,
                modifier = Modifier
                    .graphicsLayer(
                        alpha = textAlpha.value,
                        translationY = textOffsetY.value
                    )
                    .constrainAs(tagline) {
                        top.linkTo(appName.bottom, margin = taglineMargin)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )

            // 3 Feature Capsule Badges
            Row(
                modifier = Modifier
                    .graphicsLayer(
                        alpha = textAlpha.value,
                        translationY = textOffsetY.value
                    )
                    .constrainAs(featurePills) {
                        top.linkTo(tagline.bottom, margin = featurePillsMargin)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                horizontalArrangement = Arrangement.spacedBy(6.sdp()),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SplashPill(text = "🌾 मंडी भाव")
                SplashPill(text = "📰 समाचार")
                SplashPill(text = "🏪 सेवाएं")
            }

            // Sleek Modern Progress Bar
            LinearProgressIndicator(
                modifier = Modifier
                    .width(110.sdp())
                    .height(3.sdp())
                    .clip(RoundedCornerShape(3.sdp()))
                    .graphicsLayer(alpha = textAlpha.value)
                    .constrainAs(loader) {
                        bottom.linkTo(footer.top, margin = loaderMargin)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                color = Color(0xFF38C792),
                trackColor = Color(0xFFD4EFE4)
            )

            // Inspiring Footer
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .graphicsLayer(alpha = textAlpha.value)
                    .constrainAs(footer) {
                        bottom.linkTo(parent.bottom, margin = footerMargin)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ) {
                Text(
                    text = "🇮🇳 आत्मनिर्भर गाँव • डिजिटल गाँव",
                    fontSize = 12.ssp(),
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1B5E48),
                    fontFamily = NotoSansHindi
                )
                Spacer(modifier = Modifier.height(3.sdp()))
                Text(
                    text = "Apna Gaon • v${BuildConfig.VERSION_NAME}",
                    fontSize = 10.ssp(),
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF6B8F82)
                )
            }
        }
    }
}

@Composable
private fun SplashPill(text: String) {
    Surface(
        shape = RoundedCornerShape(16.sdp()),
        color = Color.White.copy(alpha = 0.85f),
        border = BorderStroke(1.sdp(), Color(0x2638C792)),
        shadowElevation = 1.sdp()
    ) {
        Text(
            text = text,
            fontSize = 11.ssp(),
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF165D47),
            fontFamily = NotoSansHindi,
            modifier = Modifier.padding(horizontal = 8.sdp(), vertical = 4.sdp())
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    AapanGavTheme {
        SplashContent()
    }
}
