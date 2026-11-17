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
import com.dv.apna.R
import com.dv.apna.core.ads.AdManager
import com.dv.apna.core.config.RemoteConfigManager
import com.dv.apna.core.navigation.Route
import com.dv.apna.core.utils.dial
import com.dv.apna.feature.health.presentation.screen.DoctorScreen
import com.dv.apna.feature.health.presentation.screen.EmergencyListScreen
import com.dv.apna.feature.health.presentation.screen.HealthHubScreen
import com.dv.apna.feature.health.presentation.screen.HospitalScreen
import com.dv.apna.feature.health.presentation.screen.PharmacyScreen
import com.dv.apna.feature.health.presentation.viewmodel.HealthViewModel

fun NavGraphBuilder.healthNavGraph(
    navController: NavHostController,
    adManager: AdManager,
    remoteConfigManager: RemoteConfigManager,
    activity: Activity?
) {
    composable<Route.Health> {
        val viewModel: HealthViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val context = LocalContext.current

        LaunchedEffect(Unit) {
            adManager.preloadInterstitialAd(context.applicationContext)
        }

        HealthHubScreen(
            state = state,
            onEvent = viewModel::onEvent,
            effect = viewModel.effect,
            remoteConfigManager = remoteConfigManager,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToDoctors = {
                if (activity != null) {
                    adManager.showInterstitialAd(activity) { navController.navigate(Route.Doctors) }
                } else {
                    navController.navigate(Route.Doctors)
                }
            },
            onNavigateToHospitals = {
                if (activity != null) {
                    adManager.showInterstitialAd(activity) { navController.navigate(Route.Hospitals) }
                } else {
                    navController.navigate(Route.Hospitals)
                }
            },
            onNavigateToPharmacy = {
                if (activity != null) {
                    adManager.showInterstitialAd(activity) { navController.navigate(Route.Pharmacy) }
                } else {
                    navController.navigate(Route.Pharmacy)
                }
            },
            onNavigateToAmbulance = {
                if (activity != null) {
                    adManager.showInterstitialAd(activity) { navController.navigate(Route.Ambulance) }
                } else {
                    navController.navigate(Route.Ambulance)
                }
            },
            onNavigateToPolice = {
                if (activity != null) {
                    adManager.showInterstitialAd(activity) { navController.navigate(Route.Police) }
                } else {
                    navController.navigate(Route.Police)
                }
            },
            onDialPhone = { phone -> context.dial(phone) }
        )
    }

    composable<Route.Doctors> {
        val viewModel: HealthViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val context = LocalContext.current
        DoctorScreen(
            state = state,
            onEvent = viewModel::onEvent,
            effect = viewModel.effect,
            remoteConfigManager = remoteConfigManager,
            onNavigateBack = { navController.popBackStack() },
            onDialPhone = { phone -> context.dial(phone) }
        )
    }

    composable<Route.Hospitals> {
        val viewModel: HealthViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val context = LocalContext.current
        HospitalScreen(
            state = state,
            onEvent = viewModel::onEvent,
            effect = viewModel.effect,
            remoteConfigManager = remoteConfigManager,
            onNavigateBack = { navController.popBackStack() },
            onDialPhone = { phone -> context.dial(phone) }
        )
    }

    composable<Route.Pharmacy> {
        val viewModel: HealthViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val context = LocalContext.current
        PharmacyScreen(
            state = state,
            onEvent = viewModel::onEvent,
            effect = viewModel.effect,
            remoteConfigManager = remoteConfigManager,
            onNavigateBack = { navController.popBackStack() },
            onDialPhone = { phone -> context.dial(phone) }
        )
    }

    composable<Route.Ambulance> {
        val viewModel: HealthViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val context = LocalContext.current
        EmergencyListScreen(
            title = stringResource(id = R.string.ambulance),
            list = state.ambulances,
            isLoading = state.isLoading,
            error = state.error,
            onEvent = viewModel::onEvent,
            effect = viewModel.effect,
            remoteConfigManager = remoteConfigManager,
            onNavigateBack = { navController.popBackStack() },
            onDialPhone = { phone -> context.dial(phone) }
        )
    }

    composable<Route.Police> {
        val viewModel: HealthViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val context = LocalContext.current
        EmergencyListScreen(
            title = stringResource(id = R.string.police),
            list = state.police,
            isLoading = state.isLoading,
            error = state.error,
            onEvent = viewModel::onEvent,
            effect = viewModel.effect,
            remoteConfigManager = remoteConfigManager,
            onNavigateBack = { navController.popBackStack() },
            onDialPhone = { phone -> context.dial(phone) }
        )
    }
}
