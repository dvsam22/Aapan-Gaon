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
import com.dv.apna.feature.legal_panchayat.presentation.effect.LegalPanchayatEffect
import com.dv.apna.feature.legal_panchayat.presentation.screen.LegalPanchayatDetailsScreen
import com.dv.apna.feature.legal_panchayat.presentation.screen.LegalPanchayatHubScreen
import com.dv.apna.feature.legal_panchayat.presentation.viewmodel.LegalPanchayatViewModel
import kotlinx.coroutines.flow.collectLatest

fun NavGraphBuilder.legalPanchayatNavGraph(
    navController: NavHostController,
    adManager: AdManager,
    remoteConfigManager: RemoteConfigManager,
    activity: Activity?
) {
    composable<Route.LegalPanchayat> {
        val context = LocalContext.current

        LaunchedEffect(Unit) {
            adManager.preloadInterstitialAd(context.applicationContext)
        }

        LegalPanchayatHubScreen(
            remoteConfigManager = remoteConfigManager,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToDetails = { categoryId, title ->
                if (activity != null) {
                    adManager.showInterstitialAd(activity) {
                        navController.navigate(Route.LegalPanchayatDetails(categoryId, title))
                    }
                } else {
                    navController.navigate(Route.LegalPanchayatDetails(categoryId, title))
                }
            }
        )
    }

    composable<Route.LegalPanchayatDetails> {
        val viewModel: LegalPanchayatViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val context = LocalContext.current

        LaunchedEffect(Unit) {
            viewModel.effect.collectLatest { effect ->
                when (effect) {
                    is LegalPanchayatEffect.DialPhone -> context.dial(effect.contact)
                    else -> {}
                }
            }
        }

        LegalPanchayatDetailsScreen(
            state = state,
            onEvent = viewModel::onEvent,
            effect = viewModel.effect,
            remoteConfigManager = remoteConfigManager,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToAddBusiness = { navController.navigate(Route.AddBusiness) }
        )
    }
}
