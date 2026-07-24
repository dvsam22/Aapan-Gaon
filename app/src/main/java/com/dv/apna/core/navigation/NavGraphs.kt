package com.dv.apna.core.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.dv.apna.core.utils.dial
import com.dv.apna.feature.home.presentation.screen.HomeScreen
import com.dv.apna.feature.home.presentation.viewmodel.HomeViewModel
import com.dv.apna.feature.mandi.presentation.screen.MandiHubScreen
import com.dv.apna.feature.mandi.presentation.screen.CropPricesScreen
import com.dv.apna.feature.mandi.presentation.screen.TodayMarketScreen
import com.dv.apna.feature.mandi.presentation.screen.LocalBuyersScreen
import com.dv.apna.feature.mandi.presentation.viewmodel.MandiViewModel
import com.dv.apna.feature.health.presentation.screen.HealthHubScreen
import com.dv.apna.feature.health.presentation.screen.DoctorScreen
import com.dv.apna.feature.health.presentation.screen.HospitalScreen
import com.dv.apna.feature.health.presentation.screen.PharmacyScreen
import com.dv.apna.feature.health.presentation.screen.EmergencyListScreen
import com.dv.apna.feature.health.presentation.viewmodel.HealthViewModel
import com.dv.apna.feature.construction.presentation.screen.ConstructionHubScreen
import com.dv.apna.feature.construction.presentation.screen.BricksSuppliersScreen
import com.dv.apna.feature.construction.presentation.screen.MaterialShopsScreen
import com.dv.apna.feature.construction.presentation.screen.HardwareShopsScreen
import com.dv.apna.feature.construction.presentation.viewmodel.ConstructionViewModel
import com.dv.apna.feature.construction.presentation.viewmodel.BricksViewModel
import com.dv.apna.feature.construction.presentation.viewmodel.MaterialShopsViewModel
import com.dv.apna.feature.construction.presentation.viewmodel.HardwareShopsViewModel
import com.dv.apna.feature.labour.presentation.screen.LabourBoardScreen
import com.dv.apna.feature.labour.presentation.screen.LabourDetailsScreen
import com.dv.apna.feature.labour.presentation.viewmodel.LabourViewModel
import com.dv.apna.feature.transport.presentation.effect.TransportEffect
import com.dv.apna.feature.transport.presentation.screen.TransportBoardScreen
import com.dv.apna.feature.transport.presentation.screen.TransportDetailsScreen
import com.dv.apna.feature.transport.presentation.viewmodel.TransportViewModel
import com.dv.apna.feature.news.presentation.screen.LocalNewsScreen
import kotlinx.coroutines.flow.collectLatest
import com.dv.apna.feature.news.presentation.screen.NewsDetailsScreen
import com.dv.apna.feature.news.presentation.screen.NoticeDetailsScreen
import com.dv.apna.feature.news.presentation.viewmodel.NewsViewModel
import com.dv.apna.feature.splash.presentation.screen.SplashScreen
import com.dv.apna.feature.splash.presentation.viewmodel.SplashViewModel
import com.dv.apna.feature.language.presentation.screen.LanguageScreen
import com.dv.apna.feature.language.presentation.screen.ChangeLanguageScreen
import com.dv.apna.feature.language.presentation.screen.ChangeVillageScreen
import com.dv.apna.feature.language.presentation.viewmodel.LanguageViewModel
import com.dv.apna.feature.settings.presentation.screen.AboutUsScreen
import com.dv.apna.feature.settings.presentation.screen.PrivacyPolicyScreen
import com.dv.apna.feature.settings.presentation.screen.TermsAndConditionsScreen
import com.dv.apna.feature.notification.presentation.screen.NotificationScreen
import com.dv.apna.feature.notification.presentation.screen.NotificationDetailsScreen
import com.dv.apna.feature.notification.presentation.viewmodel.NotificationViewModel
import com.dv.apna.feature.splash.presentation.effect.SplashEffect
import com.dv.apna.feature.family.presentation.screen.FamilyFunctionScreen
import com.dv.apna.feature.family.presentation.screen.FamilyFunctionDetailsScreen
import com.dv.apna.feature.family.presentation.viewmodel.FamilyFunctionViewModel
import com.dv.apna.feature.family.presentation.effect.FamilyFunctionEffect

@Composable
fun RootNavGraph(
    navController: NavHostController,
    startDestination: Route = Route.Splash()
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(400)
            ) + fadeIn(animationSpec = tween(400))
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(400)
            ) + fadeOut(animationSpec = tween(400))
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(400)
            ) + fadeIn(animationSpec = tween(400))
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(400)
            ) + fadeOut(animationSpec = tween(400))
        }
    ) {
        composable<Route.Splash> { backStackEntry ->
            val splashRoute: Route.Splash = backStackEntry.toRoute()
            val viewModel: SplashViewModel = hiltViewModel()
            
            // Sync the notification data from route to ViewModel
            LaunchedEffect(splashRoute) {
                if (splashRoute.notificationId != null) {
                    Log.d("FCM_DEBUG", "Splash received route data: ${splashRoute.notificationId}")
                }
            }

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
                        "news" -> navController.navigate(Route.NewsDetails(id))
                        "notice" -> navController.navigate(Route.NoticeDetails(id))
                        else -> navController.navigate(Route.NotificationDetails(id))
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
                onNavigateBack = {
                    navController.popBackStack()
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
                onNavigateToHealth = { navController.navigate(Route.Health) },
                onNavigateToFamilyFunction = { navController.navigate(Route.FamilyFunction) },
                onNavigateToLanguage = { navController.navigate(Route.ChangeLanguage) },
                onNavigateToChangeVillage = { navController.navigate(Route.ChangeVillage) },
                onNavigateToAboutUs = { navController.navigate(Route.AboutUs) },
                onNavigateToPrivacyPolicy = { navController.navigate(Route.PrivacyPolicy) },
                onNavigateToTerms = { navController.navigate(Route.TermsAndConditions) }
            )
        }

        composable<Route.Services> { }
        composable<Route.Health> {
            val viewModel: HealthViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            val context = LocalContext.current
            HealthHubScreen(
                state = state,
                onEvent = viewModel::onEvent,
                effect = viewModel.effect,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDoctors = { navController.navigate(Route.Doctors) },
                onNavigateToHospitals = { navController.navigate(Route.Hospitals) },
                onNavigateToPharmacy = { navController.navigate(Route.Pharmacy) },
                onNavigateToAmbulance = { navController.navigate(Route.Ambulance) },
                onNavigateToPolice = { navController.navigate(Route.Police) },
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
                onNavigateBack = { navController.popBackStack() },
                onDialPhone = { phone -> context.dial(phone) }
            )
        }
        composable<Route.Ambulance> {
            val viewModel: HealthViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            val context = LocalContext.current
            EmergencyListScreen(
                title = "Ambulance",
                list = state.ambulances,
                isLoading = state.isLoading,
                error = state.error,
                onEvent = viewModel::onEvent,
                effect = viewModel.effect,
                onNavigateBack = { navController.popBackStack() },
                onDialPhone = { phone -> context.dial(phone) }
            )
        }
        composable<Route.Police> {
            val viewModel: HealthViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            val context = LocalContext.current
            EmergencyListScreen(
                title = "Police",
                list = state.police,
                isLoading = state.isLoading,
                error = state.error,
                onEvent = viewModel::onEvent,
                effect = viewModel.effect,
                onNavigateBack = { navController.popBackStack() },
                onDialPhone = { phone -> context.dial(phone) }
            )
        }
        composable<Route.Mandi> {
            val viewModel: MandiViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            MandiHubScreen(
                state = state,
                onEvent = viewModel::onEvent,
                effect = viewModel.effect,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCropPrices = { navController.navigate(Route.CropPrices) },
                onNavigateToTodayMarket = { navController.navigate(Route.TodayMarket) },
                onNavigateToLocalBuyers = { navController.navigate(Route.LocalBuyers) }
            )
        }
        composable<Route.CropPrices> {
            val viewModel: MandiViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            CropPricesScreen(
                state = state,
                onEvent = viewModel::onEvent,
                effect = viewModel.effect,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable<Route.TodayMarket> {
            val viewModel: MandiViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            TodayMarketScreen(
                state = state,
                onEvent = viewModel::onEvent,
                effect = viewModel.effect,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable<Route.LocalBuyers> {
            val viewModel: MandiViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            LocalBuyersScreen(
                state = state,
                onEvent = viewModel::onEvent,
                effect = viewModel.effect,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable<Route.AboutUs> {
            AboutUsScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable<Route.PrivacyPolicy> {
            PrivacyPolicyScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable<Route.TermsAndConditions> {
            TermsAndConditionsScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable<Route.Construction> {
            val viewModel: ConstructionViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            ConstructionHubScreen(
                state = state,
                onEvent = viewModel::onEvent,
                effect = viewModel.effect,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToBricks = { navController.navigate(Route.BricksSuppliers) },
                onNavigateToMaterialShops = { navController.navigate(Route.MaterialShops) },
                onNavigateToHardwareShops = { navController.navigate(Route.HardwareShops) }
            )
        }

        composable<Route.HardwareShops> {
            val viewModel: HardwareShopsViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            HardwareShopsScreen(
                state = state,
                onEvent = viewModel::onEvent,
                effect = viewModel.effect,
                onNavigateBack = { navController.popBackStack() }
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
                onNavigateToCategory = { id, name ->
                    navController.navigate(Route.TransportDetails(id, name))
                }
            )
        }

        composable<Route.TransportDetails> {
            val viewModel: TransportViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            val context = LocalContext.current

            androidx.compose.runtime.LaunchedEffect(Unit) {
                viewModel.effect.collectLatest { effect ->
                    when (effect) {
                        is TransportEffect.DialPhone -> context.dial(effect.contact)
                        else -> {}
                    }
                }
            }

            TransportDetailsScreen(
                state = state,
                onEvent = viewModel::onEvent,
                effect = viewModel.effect,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable<Route.News> {
            val viewModel: NewsViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            LocalNewsScreen(
                state = state,
                onEvent = viewModel::onEvent,
                effect = viewModel.effect,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToNewsDetails = { id -> navController.navigate(Route.NewsDetails(id)) },
                onNavigateToNoticeDetails = { id -> navController.navigate(Route.NoticeDetails(id)) }
            )
        }

        composable<Route.NewsDetails> { backStackEntry ->
            val route: Route.NewsDetails = backStackEntry.toRoute()
            val viewModel: NewsViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            NewsDetailsScreen(
                newsId = route.id,
                state = state,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<Route.NoticeDetails> { backStackEntry ->
            val route: Route.NoticeDetails = backStackEntry.toRoute()
            val viewModel: NewsViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            NoticeDetailsScreen(
                noticeId = route.id,
                state = state,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<Route.Notifications> {
            val viewModel: NotificationViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            NotificationScreen(
                state = state,
                onEvent = viewModel::onEvent,
                effect = viewModel.effect,
                onNavigateToDetails = { id ->
                    navController.navigate(Route.NotificationDetails(id))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<Route.NotificationDetails> { backStackEntry ->
            val route: Route.NotificationDetails = backStackEntry.toRoute()
            val viewModel: NotificationViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            NotificationDetailsScreen(
                notificationId = route.id,
                state = state,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<Route.FamilyFunction> {
            FamilyFunctionScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetails = { categoryId ->
                    navController.navigate(Route.FamilyFunctionDetails(categoryId))
                }
            )
        }

        composable<Route.FamilyFunctionDetails> {
            val viewModel: FamilyFunctionViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            val context = LocalContext.current

            androidx.compose.runtime.LaunchedEffect(Unit) {
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
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<Route.Settings> { }
    }
}
