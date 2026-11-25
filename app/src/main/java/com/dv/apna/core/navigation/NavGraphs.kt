package com.dv.apna.core.navigation

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.dv.apna.core.components.LocalNavigateToAddBusiness
import com.dv.apna.core.navigation.graphs.businessNavGraph
import com.dv.apna.core.navigation.graphs.constructionNavGraph
import com.dv.apna.core.navigation.graphs.cropDoctorNavGraph
import com.dv.apna.core.navigation.graphs.kyaBanegaNavGraph
import com.dv.apna.core.navigation.graphs.educationNavGraph
import com.dv.apna.core.navigation.graphs.familyNavGraph
import com.dv.apna.core.navigation.graphs.healthNavGraph
import com.dv.apna.core.navigation.graphs.homeNavGraph
import com.dv.apna.core.navigation.graphs.labourNavGraph
import com.dv.apna.core.navigation.graphs.legalPanchayatNavGraph
import com.dv.apna.core.navigation.graphs.mandiNavGraph
import com.dv.apna.core.navigation.graphs.newsNavGraph
import com.dv.apna.core.navigation.graphs.notificationNavGraph
import com.dv.apna.core.navigation.graphs.onboardingNavGraph
import com.dv.apna.core.navigation.graphs.settingsNavGraph
import com.dv.apna.core.navigation.graphs.transportNavGraph
import dagger.hilt.android.EntryPointAccessors

private fun Context.findActivity(): Activity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) {
            return ctx
        }
        ctx = ctx.baseContext
    }
    return null
}

@Composable
fun RootNavGraph(
    navController: NavHostController,
    startDestination: Route = Route.Splash()
) {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    val entryPoint = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            NavGraphEntryPoint::class.java
        )
    }
    val remoteConfigManager = entryPoint.remoteConfigManager()
    val adManager = entryPoint.adManager()

    CompositionLocalProvider(
        LocalNavigateToAddBusiness provides { navController.navigate(Route.AddBusiness) }
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
            onboardingNavGraph(navController, remoteConfigManager)
            homeNavGraph(navController, adManager, activity)
            healthNavGraph(navController, adManager, remoteConfigManager, activity)
            mandiNavGraph(navController, adManager, remoteConfigManager, activity)
            constructionNavGraph(navController, adManager, remoteConfigManager, activity)
            labourNavGraph(navController, adManager, remoteConfigManager, activity)
            transportNavGraph(navController, adManager, remoteConfigManager, activity)
            newsNavGraph(navController, adManager, remoteConfigManager)
            notificationNavGraph(navController, remoteConfigManager)
            familyNavGraph(navController, adManager, remoteConfigManager, activity)
            educationNavGraph(navController, adManager, remoteConfigManager, activity)
            legalPanchayatNavGraph(navController, adManager, remoteConfigManager, activity)
            settingsNavGraph(navController)
            businessNavGraph(navController)
            cropDoctorNavGraph(navController, remoteConfigManager)
            kyaBanegaNavGraph(navController, remoteConfigManager)
        }
    }
}
