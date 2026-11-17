package com.dv.apna.core.navigation.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.dv.apna.core.navigation.Route
import com.dv.apna.feature.settings.presentation.screen.AboutUsScreen
import com.dv.apna.feature.settings.presentation.screen.PrivacyPolicyScreen
import com.dv.apna.feature.settings.presentation.screen.TermsAndConditionsScreen

fun NavGraphBuilder.settingsNavGraph(
    navController: NavHostController
) {
    composable<Route.AboutUs> {
        AboutUsScreen(onNavigateBack = { navController.popBackStack() })
    }
    composable<Route.PrivacyPolicy> {
        PrivacyPolicyScreen(onNavigateBack = { navController.popBackStack() })
    }
    composable<Route.TermsAndConditions> {
        TermsAndConditionsScreen(onNavigateBack = { navController.popBackStack() })
    }
}
