package com.dv.apna.core.navigation.graphs

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.dv.apna.core.ads.AdManager
import com.dv.apna.core.navigation.Route
import com.dv.apna.feature.home.presentation.screen.HomeScreen
import com.dv.apna.feature.home.presentation.viewmodel.HomeViewModel

fun NavGraphBuilder.homeNavGraph(
    navController: NavHostController,
    adManager: AdManager,
    activity: Activity?
) {
    composable<Route.Home> {
        val viewModel: HomeViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val context = LocalContext.current

        HomeScreen(
            state = state,
            onEvent = viewModel::onEvent,
            onNavigateToNotifications = {
                if (activity != null) {
                    adManager.showInterstitialAd(activity) {
                        navController.navigate(Route.Notifications)
                    }
                } else {
                    navController.navigate(Route.Notifications)
                }
            },
            onNavigateToConstruction = { navController.navigate(Route.Construction) },
            onNavigateToLabour = { navController.navigate(Route.LabourBoard) },
            onNavigateToTransport = { navController.navigate(Route.Transport) },
            onNavigateToMandi = { navController.navigate(Route.Mandi) },
            onNavigateToNews = { navController.navigate(Route.News) },
            onNavigateToHealth = { navController.navigate(Route.Health) },
            onNavigateToFamilyFunction = { navController.navigate(Route.FamilyFunction) },
            onNavigateToEducation = {
                if (activity != null) {
                    adManager.showInterstitialAd(activity) { navController.navigate(Route.Education) }
                } else {
                    navController.navigate(Route.Education)
                }
            },
            onNavigateToLegalPanchayat = {
                if (activity != null) {
                    adManager.showInterstitialAd(activity) { navController.navigate(Route.LegalPanchayat) }
                } else {
                    navController.navigate(Route.LegalPanchayat)
                }
            },
            onNavigateToLanguage = {
                if (activity != null) {
                    adManager.showInterstitialAd(activity) {
                        navController.navigate(Route.ChangeLanguage)
                    }
                } else {
                    navController.navigate(Route.ChangeLanguage)
                }
            },
            onNavigateToChangeVillage = {
                if (activity != null) {
                    adManager.showInterstitialAd(activity) {
                        navController.navigate(Route.ChangeVillage)
                    }
                } else {
                    navController.navigate(Route.ChangeVillage)
                }
            },
            onNavigateToAboutUs = { navController.navigate(Route.AboutUs) },
            onNavigateToAddBusiness = {
                if (activity != null) {
                    adManager.showInterstitialAd(activity) {
                        navController.navigate(Route.AddBusiness)
                    }
                } else {
                    navController.navigate(Route.AddBusiness)
                }
            },
            onNavigateToPrivacyPolicy = { navController.navigate(Route.PrivacyPolicy) },
            onNavigateToTerms = { navController.navigate(Route.TermsAndConditions) },
            onNavigateToCropDoctor = { navController.navigate(Route.CropDoctor) },
            onNavigateToKyaBanega = { navController.navigate(Route.KyaBanega) },
            onBannerClick = { banner ->
                when (banner.link.lowercase().trim()) {
                    "crop_doctor", "fasal_doctor", "kisan_doctor", "doctor" -> {
                        navController.navigate(Route.CropDoctor)
                    }
                    "kya_banega", "food_assistant", "recipe", "khana", "rasoi" -> {
                        navController.navigate(Route.KyaBanega)
                    }
                    "ration_calculator", "ration", "food_calculator", "catering", "bhandara" -> {
                        navController.navigate(Route.RationCalculator)
                    }
                    "today_market", "mandi", "market" -> {
                        navController.navigate(Route.TodayMarket)
                    }
                    "news", "samachar" -> {
                        navController.navigate(Route.News)
                    }
                    "add_business", "register_shop", "business" -> {
                        navController.navigate(Route.AddBusiness)
                    }
                    "local_buyers", "buyers", "kisan_market" -> {
                        navController.navigate(Route.LocalBuyers)
                    }
                    "health", "emergency", "ambulance", "police" -> {
                        navController.navigate(Route.Health)
                    }
                    else -> {
                        if (banner.link.startsWith("http://") || banner.link.startsWith("https://")) {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(banner.link)).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                                context.startActivity(intent)
                            } catch (_: Exception) {
                            }
                        }
                    }
                }
            }
        )
    }
}
