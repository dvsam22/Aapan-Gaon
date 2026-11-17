package com.dv.apna.core.navigation.graphs

import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.dv.apna.core.config.RemoteConfigManager
import com.dv.apna.core.navigation.Route
import com.dv.apna.feature.notification.presentation.screen.NotificationDetailsScreen
import com.dv.apna.feature.notification.presentation.screen.NotificationScreen
import com.dv.apna.feature.notification.presentation.viewmodel.NotificationViewModel

fun NavGraphBuilder.notificationNavGraph(
    navController: NavHostController,
    remoteConfigManager: RemoteConfigManager
) {
    composable<Route.Notifications> {
        val viewModel: NotificationViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        NotificationScreen(
            state = state,
            onEvent = viewModel::onEvent,
            effect = viewModel.effect,
            remoteConfigManager = remoteConfigManager,
            onNavigateToDetails = { id ->
                navController.navigate(Route.NotificationDetails(id))
            },
            onNavigateBack = { navController.popBackStack() }
        )
    }

    composable<Route.NotificationDetails> { backStackEntry ->
        val route: Route.NotificationDetails = backStackEntry.toRoute()
        val viewModel: NotificationViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        NotificationDetailsScreen(
            notificationId = route.id,
            state = state,
            onNavigateBack = { navController.popBackStack() }
        )
    }
}
