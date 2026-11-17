package com.dv.apna.core.navigation.graphs

import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.dv.apna.core.navigation.Route
import com.dv.apna.feature.business.presentation.screen.AddBusinessScreen
import com.dv.apna.feature.business.presentation.viewmodel.AddBusinessViewModel

fun NavGraphBuilder.businessNavGraph(
    navController: NavHostController
) {
    composable<Route.AddBusiness> {
        val viewModel: AddBusinessViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        AddBusinessScreen(
            state = state,
            onEvent = viewModel::onEvent,
            onNavigateBack = { navController.popBackStack() }
        )
    }
}
