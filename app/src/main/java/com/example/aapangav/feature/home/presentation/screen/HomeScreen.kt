package com.example.aapangav.feature.home.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.aapangav.core.components.AapanGavLoading
import com.example.aapangav.core.components.AapanGavToolbar
import com.example.aapangav.core.components.AapanGavErrorScreen
import com.example.aapangav.feature.home.presentation.state.HomeState
import com.example.aapangav.feature.home.presentation.event.HomeEvent

@Composable
fun HomeScreen(
    state: HomeState,
    onEvent: (HomeEvent) -> Unit
) {
    Scaffold(
        topBar = {
            AapanGavToolbar(title = "Aapan Gav")
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (state.isLoading) {
                AapanGavLoading()
            } else if (state.error != null) {
                AapanGavErrorScreen(
                    message = state.error,
                    onRetry = { onEvent(HomeEvent.Refresh) }
                )
            } else {
                // Display Home Content
            }
        }
    }
}