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
import com.dv.apna.feature.mandi.presentation.screen.LocalBuyersScreen
import com.dv.apna.feature.mandi.presentation.screen.MandiHubScreen
import com.dv.apna.feature.mandi.presentation.screen.TodayMarketScreen
import com.dv.apna.feature.mandi.presentation.viewmodel.MandiViewModel

fun NavGraphBuilder.mandiNavGraph(
    navController: NavHostController,
    adManager: AdManager,
    remoteConfigManager: RemoteConfigManager,
    activity: Activity?
) {
    composable<Route.Mandi> {
        val viewModel: MandiViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val context = LocalContext.current

        LaunchedEffect(Unit) {
            adManager.preloadInterstitialAd(context.applicationContext)
        }

        MandiHubScreen(
            state = state,
            onEvent = viewModel::onEvent,
            effect = viewModel.effect,
            remoteConfigManager = remoteConfigManager,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToTodayMarket = {
                if (activity != null) {
                    adManager.showInterstitialAd(activity) { navController.navigate(Route.TodayMarket) }
                } else {
                    navController.navigate(Route.TodayMarket)
                }
            },
            onNavigateToLocalBuyers = {
                if (activity != null) {
                    adManager.showInterstitialAd(activity) { navController.navigate(Route.LocalBuyers) }
                } else {
                    navController.navigate(Route.LocalBuyers)
                }
            }
        )
    }

    composable<Route.TodayMarket> {
        val viewModel: MandiViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        TodayMarketScreen(
            state = state,
            onEvent = viewModel::onEvent,
            effect = viewModel.effect,
            remoteConfigManager = remoteConfigManager,
            onNavigateBack = { navController.popBackStack() }
        )
    }

    composable<Route.LocalBuyers> {
        val viewModel: MandiViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        LocalBuyersScreen(
            state = state,
            onEvent = viewModel::onEvent,
            effect = viewModel.effect,
            remoteConfigManager = remoteConfigManager,
            onNavigateBack = { navController.popBackStack() }
        )
    }
}
