package com.dv.apna.core.navigation.graphs

import android.app.Activity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.dv.apna.core.ads.AdManager
import com.dv.apna.core.config.RemoteConfigManager
import com.dv.apna.core.navigation.Route
import com.dv.apna.core.utils.dial
import com.dv.apna.feature.transport.presentation.effect.TransportEffect
import com.dv.apna.feature.transport.presentation.screen.TransportBoardScreen
import com.dv.apna.feature.transport.presentation.screen.TransportDetailsScreen
import com.dv.apna.feature.transport.presentation.viewmodel.TransportViewModel
import kotlinx.coroutines.flow.collectLatest

fun NavGraphBuilder.transportNavGraph(
    navController: NavHostController,
    adManager: AdManager,
    remoteConfigManager: RemoteConfigManager,
    activity: Activity?
) {
    composable<Route.Transport> {
        val viewModel: TransportViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val context = LocalContext.current

        LaunchedEffect(Unit) {
            adManager.preloadInterstitialAd(context.applicationContext)
        }

        TransportBoardScreen(
            state = state,
            onEvent = viewModel::onEvent,
            effect = viewModel.effect,
            remoteConfigManager = remoteConfigManager,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToCategory = { id, name ->
                if (activity != null) {
                    adManager.showInterstitialAd(activity) { navController.navigate(Route.TransportDetails(id, name)) }
                } else {
                    navController.navigate(Route.TransportDetails(id, name))
                }
            }
        )
    }

    composable<Route.TransportDetails> {
        val viewModel: TransportViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val context = LocalContext.current

        LaunchedEffect(Unit) {
            viewModel.effect.collectLatest { effect ->
                when (effect) {
                    is TransportEffect.DialPhone -> context.dial(effect.contact)
                    else -> {}
                }
            }
        }

        TransportDetailsScreen(
            state = state,
            onEvent = viewModel::onEvent,
            effect = viewModel.effect,
            remoteConfigManager = remoteConfigManager,
            onNavigateBack = { navController.popBackStack() }
        )
    }
}
