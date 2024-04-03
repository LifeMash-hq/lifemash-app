package org.bmsk.lifemash.feature.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.bmsk.lifemash.feature.topic.navigation.TopicRoute
import org.bmsk.lifemash.feature.topic.navigation.navigateWebView

internal class MainNavigator(
    val navController: NavHostController,
) {
//    private val currentDestination: NavDestination?
//        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    val startDestination = TopicRoute.route

    fun navigateWebView(url: String) {
        navController.navigateWebView(url)
    }
}

@Composable
internal fun rememberMainNavigator(
    navController: NavHostController = rememberNavController(),
): MainNavigator = remember(navController) {
    MainNavigator(navController = navController)
}
