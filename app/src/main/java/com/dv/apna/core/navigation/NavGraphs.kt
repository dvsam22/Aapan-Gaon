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
import com.dv.apna.feature.construction.presentation.screen.ConstructionHubScreen
import com.dv.apna.feature.construction.presentation.screen.BricksSuppliersScreen
import com.dv.apna.feature.construction.presentation.screen.MaterialShopsScreen
import com.dv.apna.feature.construction.presentation.viewmodel.ConstructionViewModel
import com.dv.apna.feature.construction.presentation.viewmodel.BricksViewModel
import com.dv.apna.feature.construction.presentation.viewmodel.MaterialShopsViewModel
import com.dv.apna.feature.labour.presentation.screen.LabourBoardScreen
import com.dv.apna.feature.labour.presentation.screen.LabourDetailsScreen
import com.dv.apna.feature.labour.presentation.viewmodel.LabourViewModel
import com.dv.apna.feature.transport.presentation.screen.TransportBoardScreen
import com.dv.apna.feature.transport.presentation.screen.TransportDetailsScreen
import com.dv.apna.feature.transport.presentation.viewmodel.TransportViewModel
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
                onNavigateToLabour = { navController.navigate(Route.LabourBoard) },
                onNavigateToTransport = { navController.navigate(Route.Transport) },
                onNavigateToMandi = { navController.navigate(Route.Mandi) },
                onNavigateToNews = { navController.navigate(Route.News) },
                onNavigateToHealth = { navController.navigate(Route.Health) }
            )
        }

        composable<Route.Services> { }
        composable<Route.Health> { }
        composable<Route.Mandi> { }
        composable<Route.Construction> {
            val viewModel: ConstructionViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            ConstructionHubScreen(
                state = state,
                onEvent = viewModel::onEvent,
                effect = viewModel.effect,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToBricks = { navController.navigate(Route.BricksSuppliers) },
                onNavigateToMaterialShops = { navController.navigate(Route.MaterialShops) }
            )
        }

        composable<Route.BricksSuppliers> {
            val viewModel: BricksViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            BricksSuppliersScreen(
                state = state,
                onEvent = viewModel::onEvent,
                effect = viewModel.effect,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<Route.MaterialShops> {
            val viewModel: MaterialShopsViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            MaterialShopsScreen(
                state = state,
                onEvent = viewModel::onEvent,
                effect = viewModel.effect,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<Route.LabourBoard> {
            val viewModel: LabourViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            LabourBoardScreen(
                state = state,
                onEvent = viewModel::onEvent,
                effect = viewModel.effect,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCategory = { category ->
                    navController.navigate(Route.LabourDetails(category))
                }
            )
        }

        composable<Route.LabourDetails> {
            val viewModel: LabourViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            LabourDetailsScreen(
                state = state,
                onEvent = viewModel::onEvent,
                effect = viewModel.effect,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable<Route.Transport> {
            val viewModel: TransportViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            TransportBoardScreen(
                state = state,
                onEvent = viewModel::onEvent,
                effect = viewModel.effect,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCategory = { category ->
                    navController.navigate(Route.TransportDetails(category))
                }
            )
        }

        composable<Route.TransportDetails> {
            val viewModel: TransportViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            TransportDetailsScreen(
                state = state,
                onEvent = viewModel::onEvent,
                effect = viewModel.effect,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable<Route.News> { }
        composable<Route.Notifications> { }
        composable<Route.Settings> { }
    }
}
