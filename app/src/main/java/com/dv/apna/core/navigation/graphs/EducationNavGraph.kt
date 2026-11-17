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
import com.dv.apna.feature.education.presentation.effect.EducationEffect
import com.dv.apna.feature.education.presentation.screen.EducationDetailsScreen
import com.dv.apna.feature.education.presentation.screen.EducationHubScreen
import com.dv.apna.feature.education.presentation.viewmodel.EducationViewModel
import kotlinx.coroutines.flow.collectLatest

fun NavGraphBuilder.educationNavGraph(
    navController: NavHostController,
    adManager: AdManager,
    remoteConfigManager: RemoteConfigManager,
    activity: Activity?
) {
    composable<Route.Education> {
        val context = LocalContext.current

        LaunchedEffect(Unit) {
            adManager.preloadInterstitialAd(context.applicationContext)
        }

        EducationHubScreen(
            remoteConfigManager = remoteConfigManager,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToDetails = { categoryId, title ->
                if (activity != null) {
                    adManager.showInterstitialAd(activity) {
                        navController.navigate(Route.EducationDetails(categoryId, title))
                    }
                } else {
                    navController.navigate(Route.EducationDetails(categoryId, title))
                }
            }
        )
    }

    composable<Route.EducationDetails> {
        val viewModel: EducationViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val context = LocalContext.current

        LaunchedEffect(Unit) {
            viewModel.effect.collectLatest { effect ->
                when (effect) {
                    is EducationEffect.DialPhone -> context.dial(effect.contact)
                    else -> {}
                }
            }
        }

        EducationDetailsScreen(
            state = state,
            onEvent = viewModel::onEvent,
            effect = viewModel.effect,
            remoteConfigManager = remoteConfigManager,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToAddBusiness = { navController.navigate(Route.AddBusiness) }
        )
    }
}
