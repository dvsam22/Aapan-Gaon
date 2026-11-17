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
import com.dv.apna.feature.labour.presentation.screen.LabourBoardScreen
import com.dv.apna.feature.labour.presentation.screen.LabourDetailsScreen
import com.dv.apna.feature.labour.presentation.viewmodel.LabourViewModel

fun NavGraphBuilder.labourNavGraph(
    navController: NavHostController,
    adManager: AdManager,
    remoteConfigManager: RemoteConfigManager,
    activity: Activity?
) {
    composable<Route.LabourBoard> {
        val viewModel: LabourViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val context = LocalContext.current

        LaunchedEffect(Unit) {
            adManager.preloadInterstitialAd(context.applicationContext)
        }

        LabourBoardScreen(
            state = state,
            onEvent = viewModel::onEvent,
            effect = viewModel.effect,
            remoteConfigManager = remoteConfigManager,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToCategory = { category ->
                if (activity != null) {
                    adManager.showInterstitialAd(activity) { navController.navigate(Route.LabourDetails(category)) }
                } else {
                    navController.navigate(Route.LabourDetails(category))
                }
            }
        )
    }

    composable<Route.LabourDetails> {
        val viewModel: LabourViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        LabourDetailsScreen(
            state = state,
            onEvent = viewModel::onEvent,
            effect = viewModel.effect,
            remoteConfigManager = remoteConfigManager,
            onNavigateBack = { navController.popBackStack() }
        )
    }
}
