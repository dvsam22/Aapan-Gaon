package com.dv.apna.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.dv.apna.feature.home.presentation.screen.HomeScreen
import com.dv.apna.feature.home.presentation.viewmodel.HomeViewModel
import com.dv.apna.feature.splash.presentation.screen.SplashScreen
import com.dv.apna.feature.splash.presentation.viewmodel.SplashViewModel
import com.dv.apna.feature.language.presentation.screen.LanguageScreen
import com.dv.apna.feature.language.presentation.viewmodel.LanguageViewModel

@Composable
fun RootNavGraph(
    navController: NavHostController,
    startDestination: Route = Route.Splash
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<Route.Splash> {
            val viewModel: SplashViewModel = hiltViewModel()
            SplashScreen(
                viewModel = viewModel,
                onNextScreen = {
                    navController.navigate(Route.Language) {
                        popUpTo(Route.Splash) { inclusive = true }
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

        composable<Route.Home> {
            val viewModel: HomeViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            HomeScreen(
                state = state,
                onEvent = viewModel::onEvent,
                onNavigateToNotifications = { navController.navigate(Route.Notifications) },
                onNavigateToConstruction = { navController.navigate(Route.Construction) },
                onNavigateToLabour = { /* TODO: Route.Labour if it exists */ },
                onNavigateToTransport = { navController.navigate(Route.Transport) },
                onNavigateToMandi = { navController.navigate(Route.Mandi) },
                onNavigateToNews = { navController.navigate(Route.News) },
                onNavigateToHealth = { navController.navigate(Route.Health) }
            )
        }

        composable<Route.Services> { }
        composable<Route.Health> { }
        composable<Route.Mandi> { }
        composable<Route.Construction> { }
        composable<Route.Transport> { }
        composable<Route.News> { }
        composable<Route.Notifications> { }
        composable<Route.Settings> { }
    }
}
