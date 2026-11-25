package com.dv.apna.core.navigation.graphs

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.dv.apna.core.config.RemoteConfigManager
import com.dv.apna.core.navigation.Route
import com.dv.apna.feature.kya_banega.presentation.event.KyaBanegaEvent
import com.dv.apna.feature.kya_banega.presentation.screen.KyaBanegaScreen
import com.dv.apna.feature.kya_banega.presentation.state.KitchenHubTab
import com.dv.apna.feature.kya_banega.presentation.viewmodel.KyaBanegaViewModel

fun NavGraphBuilder.kyaBanegaNavGraph(
    navController: NavHostController,
    remoteConfigManager: RemoteConfigManager
) {
    composable<Route.KyaBanega> {
        val viewModel: KyaBanegaViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()

        KyaBanegaScreen(
            state = state,
            onEvent = viewModel::onEvent,
            effect = viewModel.effect,
            remoteConfigManager = remoteConfigManager,
            onNavigateBack = { navController.popBackStack() }
        )
    }

    composable<Route.RationCalculator> {
        val viewModel: KyaBanegaViewModel = hiltViewModel()
        LaunchedEffect(Unit) {
            viewModel.onEvent(KyaBanegaEvent.OnSelectHubTab(KitchenHubTab.RATION_CALCULATOR))
        }
        val state by viewModel.state.collectAsStateWithLifecycle()

        KyaBanegaScreen(
            state = state,
            onEvent = viewModel::onEvent,
            effect = viewModel.effect,
            remoteConfigManager = remoteConfigManager,
            onNavigateBack = { navController.popBackStack() }
        )
    }
}
