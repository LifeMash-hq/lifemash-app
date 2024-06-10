package org.bmsk.lifemash.feature.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.bmsk.lifemash.feature.scrap.api.ScrapNavController
import org.bmsk.lifemash.feature.topic.navigation.TopicRoute
import org.bmsk.lifemash.feature.topic.navigation.navigateWebView

internal class MainNavigator(
    val navController: NavHostController,
    private val scrapNavController: ScrapNavController,
) {
    private val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    val startDestination = TopicRoute.route

    fun navigateWebView(url: String) {
        navController.navigateWebView(url)
    }

    fun navigateScrap() {
        scrapNavController.navigate(navController, Unit)
    }
}

@Composable
internal fun rememberMainNavigator(
    scrapNavController: ScrapNavController,
    navController: NavHostController = rememberNavController(),
): MainNavigator = remember(navController) {
    MainNavigator(
        scrapNavController = scrapNavController,
        navController = navController
    )
}
