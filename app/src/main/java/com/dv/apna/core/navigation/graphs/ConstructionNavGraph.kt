package com.dv.apna.core.navigation.graphs

import android.app.Activity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.dv.apna.R
import com.dv.apna.core.ads.AdManager
import com.dv.apna.core.config.RemoteConfigManager
import com.dv.apna.core.navigation.Route
import com.dv.apna.feature.construction.presentation.event.ConstructionDetailEvent
import com.dv.apna.feature.construction.presentation.screen.BricksSuppliersScreen
import com.dv.apna.feature.construction.presentation.screen.ConstructionDetailScreen
import com.dv.apna.feature.construction.presentation.screen.ConstructionHubScreen
import com.dv.apna.feature.construction.presentation.screen.HardwareShopsScreen
import com.dv.apna.feature.construction.presentation.screen.MaterialShopsScreen
import com.dv.apna.feature.construction.presentation.viewmodel.BricksViewModel
import com.dv.apna.feature.construction.presentation.viewmodel.ConstructionDetailViewModel
import com.dv.apna.feature.construction.presentation.viewmodel.ConstructionViewModel
import com.dv.apna.feature.construction.presentation.viewmodel.HardwareShopsViewModel
import com.dv.apna.feature.construction.presentation.viewmodel.MaterialShopsViewModel

fun NavGraphBuilder.constructionNavGraph(
    navController: NavHostController,
    adManager: AdManager,
    remoteConfigManager: RemoteConfigManager,
    activity: Activity?
) {
    composable<Route.Construction> {
        val viewModel: ConstructionViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val context = LocalContext.current
        val linterTitle = stringResource(id = R.string.linter_machine)
        val shutteringTitle = stringResource(id = R.string.shuttering)

        LaunchedEffect(Unit) {
            adManager.preloadInterstitialAd(context.applicationContext)
        }

        ConstructionHubScreen(
            state = state,
            onEvent = viewModel::onEvent,
            effect = viewModel.effect,
            remoteConfigManager = remoteConfigManager,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToBricks = {
                if (activity != null) {
                    adManager.showInterstitialAd(activity) { navController.navigate(Route.BricksSuppliers) }
                } else {
                    navController.navigate(Route.BricksSuppliers)
                }
            },
            onNavigateToMaterialShops = {
                if (activity != null) {
                    adManager.showInterstitialAd(activity) { navController.navigate(Route.MaterialShops) }
                } else {
                    navController.navigate(Route.MaterialShops)
                }
            },
            onNavigateToHardwareShops = {
                if (activity != null) {
                    adManager.showInterstitialAd(activity) { navController.navigate(Route.HardwareShops) }
                } else {
                    navController.navigate(Route.HardwareShops)
                }
            },
            onNavigateToLinterMachine = {
                if (activity != null) {
                    adManager.showInterstitialAd(activity) { navController.navigate(Route.ConstructionDetail("linter_machine", linterTitle)) }
                } else {
                    navController.navigate(Route.ConstructionDetail("linter_machine", linterTitle))
                }
            },
            onNavigateToShuttering = {
                if (activity != null) {
                    adManager.showInterstitialAd(activity) { navController.navigate(Route.ConstructionDetail("shuttering", shutteringTitle)) }
                } else {
                    navController.navigate(Route.ConstructionDetail("shuttering", shutteringTitle))
                }
            }
        )
    }

    composable<Route.ConstructionDetail> { backStackEntry ->
        val route: Route.ConstructionDetail = backStackEntry.toRoute()
        val viewModel: ConstructionDetailViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()

        LaunchedEffect(route.categoryId) {
            viewModel.onEvent(ConstructionDetailEvent.LoadItems(route.categoryId))
        }

        ConstructionDetailScreen(
            title = route.title,
            state = state,
            onEvent = viewModel::onEvent,
            effect = viewModel.effect,
            remoteConfigManager = remoteConfigManager,
            onNavigateBack = { navController.popBackStack() }
        )
    }

    composable<Route.HardwareShops> {
        val viewModel: HardwareShopsViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        HardwareShopsScreen(
            state = state,
            onEvent = viewModel::onEvent,
            effect = viewModel.effect,
            remoteConfigManager = remoteConfigManager,
            onNavigateBack = { navController.popBackStack() }
        )
    }

    composable<Route.BricksSuppliers> {
        val viewModel: BricksViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        BricksSuppliersScreen(
            state = state,
            onEvent = viewModel::onEvent,
            effect = viewModel.effect,
            remoteConfigManager = remoteConfigManager,
            onNavigateBack = { navController.popBackStack() }
        )
    }

    composable<Route.MaterialShops> {
        val viewModel: MaterialShopsViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        MaterialShopsScreen(
            state = state,
            onEvent = viewModel::onEvent,
            effect = viewModel.effect,
            remoteConfigManager = remoteConfigManager,
            onNavigateBack = { navController.popBackStack() }
        )
    }
}
