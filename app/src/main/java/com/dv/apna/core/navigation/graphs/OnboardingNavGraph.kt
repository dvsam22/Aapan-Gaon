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
import com.dv.apna.feature.language.presentation.screen.ChangeLanguageScreen
import com.dv.apna.feature.language.presentation.screen.ChangeVillageScreen
import com.dv.apna.feature.language.presentation.screen.LanguageScreen
import com.dv.apna.feature.language.presentation.viewmodel.LanguageViewModel
import com.dv.apna.feature.splash.presentation.screen.SplashScreen
import com.dv.apna.feature.splash.presentation.viewmodel.SplashViewModel

fun NavGraphBuilder.onboardingNavGraph(
    navController: NavHostController,
    remoteConfigManager: RemoteConfigManager
) {
    composable<Route.Splash> { backStackEntry ->
        val splashRoute: Route.Splash = backStackEntry.toRoute()
        val viewModel: SplashViewModel = hiltViewModel()

        SplashScreen(
            viewModel = viewModel,
            onNavigateToHome = {
                navController.navigate(Route.Home) {
                    popUpTo(Route.Splash()) { inclusive = true }
                }
            },
            onNavigateToLanguage = {
                navController.navigate(Route.Language) {
                    popUpTo(Route.Splash()) { inclusive = true }
                }
            },
            onNavigateToDetails = { id, type ->
                navController.navigate(Route.Home) {
                    popUpTo(Route.Splash()) { inclusive = true }
                }
                when (type?.lowercase()) {
                    "news" -> if (id.isNotBlank()) navController.navigate(Route.NewsDetails(id)) else navController.navigate(Route.News)
                    "notice" -> if (id.isNotBlank()) navController.navigate(Route.NoticeDetails(id)) else navController.navigate(Route.News)
                    "mandi", "mandi_buyers" -> navController.navigate(Route.TodayMarket)
                    "family", "family_tent", "family_catering", "family_dj", "family_halls" -> navController.navigate(Route.FamilyFunction)
                    "labour" -> navController.navigate(Route.LabourBoard)
                    "construction" -> navController.navigate(Route.Construction)
                    "transport" -> navController.navigate(Route.Transport)
                    "health" -> navController.navigate(Route.Health)
                    "add_business" -> navController.navigate(Route.AddBusiness)
                    "home", "bounce" -> { /* Open Home Dashboard directly */ }
                    else -> if (id.isNotBlank()) navController.navigate(Route.NotificationDetails(id)) else navController.navigate(Route.Home)
                }
            }
        )
    }

    composable<Route.Language> {
        val viewModel: LanguageViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        LanguageScreen(
            state = state,
            onEvent = viewModel::onEvent,
            effect = viewModel.effect,
            onNavigateToHome = {
                navController.navigate(Route.Home) {
                    popUpTo(Route.Language) { inclusive = true }
                }
            }
        )
    }

    composable<Route.ChangeLanguage> {
        val viewModel: LanguageViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        ChangeLanguageScreen(
            state = state,
            onEvent = viewModel::onEvent,
            effect = viewModel.effect,
            remoteConfigManager = remoteConfigManager,
            onNavigateBack = {
                navController.popBackStack()
            }
        )
    }

    composable<Route.ChangeVillage> {
        val viewModel: LanguageViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        ChangeVillageScreen(
            state = state,
            onEvent = viewModel::onEvent,
            effect = viewModel.effect,
            remoteConfigManager = remoteConfigManager,
            onNavigateBack = {
                navController.popBackStack()
            }
        )
    }
}
