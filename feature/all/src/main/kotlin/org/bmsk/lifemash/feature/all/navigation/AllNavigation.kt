package org.bmsk.lifemash.feature.all.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.bmsk.lifemash.feature.all.AllRoute

fun NavController.navigateAll() {
    navigate(AllNavigation.route)
}

fun NavGraphBuilder.allNavGraph() {
    composable(route = AllNavigation.route) {
        AllRoute()
    }
}

object AllNavigation {
    const val route = "all"
}
