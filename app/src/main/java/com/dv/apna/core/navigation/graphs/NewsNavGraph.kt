package com.dv.apna.core.navigation.graphs

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.dv.apna.core.ads.AdManager
import com.dv.apna.core.config.RemoteConfigManager
import com.dv.apna.core.navigation.Route
import com.dv.apna.feature.news.presentation.screen.LocalNewsScreen
import com.dv.apna.feature.news.presentation.screen.NewsDetailsScreen
import com.dv.apna.feature.news.presentation.screen.NoticeDetailsScreen
import com.dv.apna.feature.news.presentation.viewmodel.NewsViewModel

fun NavGraphBuilder.newsNavGraph(
    navController: NavHostController,
    adManager: AdManager,
    remoteConfigManager: RemoteConfigManager
) {
    composable<Route.News> {
        val viewModel: NewsViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val context = LocalContext.current

        LaunchedEffect(Unit) {
            adManager.preloadInterstitialAd(context.applicationContext)
        }

        LocalNewsScreen(
            state = state,
            onEvent = viewModel::onEvent,
            effect = viewModel.effect,
            remoteConfigManager = remoteConfigManager,
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
            onNavigateBack = { navController.popBackStack() },
            onNavigateToNewsList = {
                navController.navigate(Route.News) {
                    popUpTo(route) { inclusive = true }
                }
            }
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
}
