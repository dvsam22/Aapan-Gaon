package com.example.aapangav.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.aapangav.feature.home.presentation.screen.HomeScreen
import com.example.aapangav.feature.home.presentation.viewmodel.HomeViewModel
import com.example.aapangav.feature.splash.presentation.screen.SplashScreen
import com.example.aapangav.feature.splash.presentation.viewmodel.SplashViewModel
import com.example.aapangav.feature.language.presentation.screen.LanguageScreen
import com.example.aapangav.feature.language.presentation.viewmodel.LanguageViewModel

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
                    navController.navigate(Route.MainGraph) {
                        popUpTo(Route.Language) { inclusive = true }
                    }
                }
            )
        }

        composable<Route.MainGraph> {
            MainNavGraph(navController = navController)
        }
    }
}

@Composable
fun MainNavGraph(
    navController: NavHostController
) {
    // This graph handles all main features without a bottom bar
    NavHost(
        navController = navController,
        startDestination = Route.Home
    ) {
        composable<Route.Home> {
            val viewModel: HomeViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            HomeScreen(
                state = state,
                onEvent = viewModel::onEvent
            )
        }
        composable<Route.Services> {
            // ServicesScreen placeholder
        }
        composable<Route.Health> {
            // HealthScreen placeholder
        }
        composable<Route.Mandi> {
            // MandiScreen placeholder
        }
        composable<Route.Construction> {
            // ConstructionScreen placeholder
        }
        composable<Route.Transport> {
            // TransportScreen placeholder
        }
        composable<Route.News> {
            // NewsScreen placeholder
        }
        composable<Route.Notifications> {
            // NotificationsScreen placeholder
        }
        composable<Route.Settings> {
            // SettingsScreen placeholder
        }
    }
}