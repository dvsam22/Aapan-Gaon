package com.example.aapangav.feature.splash.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.aapangav.R
import com.example.aapangav.core.theme.AapanGavTheme
import com.example.aapangav.core.utils.sdp
import com.example.aapangav.feature.splash.presentation.effect.SplashEffect
import com.example.aapangav.feature.splash.presentation.viewmodel.SplashViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    onNextScreen: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is SplashEffect.NavigateToNext -> onNextScreen()
            }
        }
    }

    SplashContent()
}

@Composable
fun SplashContent() {
    val logoSize = 158.sdp()
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding()
    ) {
        val logo = createRef()

        Image(
            painter = painterResource(id = R.drawable.iv_splash_logo),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(logoSize)
                .constrainAs(logo) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
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
