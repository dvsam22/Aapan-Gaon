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
import com.dv.apna.feature.family.presentation.effect.FamilyFunctionEffect
import com.dv.apna.feature.family.presentation.screen.FamilyFunctionDetailsScreen
import com.dv.apna.feature.family.presentation.screen.FamilyFunctionScreen
import com.dv.apna.feature.family.presentation.viewmodel.FamilyFunctionViewModel
import kotlinx.coroutines.flow.collectLatest

fun NavGraphBuilder.familyNavGraph(
    navController: NavHostController,
    adManager: AdManager,
    remoteConfigManager: RemoteConfigManager,
    activity: Activity?
) {
    composable<Route.FamilyFunction> {
        val context = LocalContext.current

        LaunchedEffect(Unit) {
            adManager.preloadInterstitialAd(context.applicationContext)
        }

        FamilyFunctionScreen(
            remoteConfigManager = remoteConfigManager,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToDetails = { categoryId ->
                if (activity != null) {
                    adManager.showInterstitialAd(activity) { navController.navigate(Route.FamilyFunctionDetails(categoryId)) }
                } else {
                    navController.navigate(Route.FamilyFunctionDetails(categoryId))
                }
            }
        )
    }

    composable<Route.FamilyFunctionDetails> {
        val viewModel: FamilyFunctionViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val context = LocalContext.current

        LaunchedEffect(Unit) {
            viewModel.effect.collectLatest { effect ->
                when (effect) {
                    is FamilyFunctionEffect.DialPhone -> context.dial(effect.contact)
                    else -> {}
                }
            }
        }

        FamilyFunctionDetailsScreen(
            state = state,
            onEvent = viewModel::onEvent,
            effect = viewModel.effect,
            remoteConfigManager = remoteConfigManager,
            onNavigateBack = { navController.popBackStack() }
        )
    }
}
