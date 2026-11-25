package com.dv.apna.core.navigation.graphs

import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.dv.apna.core.config.RemoteConfigManager
import com.dv.apna.core.navigation.Route
import com.dv.apna.feature.crop_doctor.presentation.screen.CropDoctorScreen
import com.dv.apna.feature.crop_doctor.presentation.viewmodel.CropDoctorViewModel

fun NavGraphBuilder.cropDoctorNavGraph(
    navController: NavHostController,
    remoteConfigManager: RemoteConfigManager
) {
    composable<Route.CropDoctor> {
        val viewModel: CropDoctorViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()

        CropDoctorScreen(
            state = state,
            onEvent = viewModel::onEvent,
            effect = viewModel.effect,
            remoteConfigManager = remoteConfigManager,
            onNavigateBack = { navController.popBackStack() }
        )
    }
}
