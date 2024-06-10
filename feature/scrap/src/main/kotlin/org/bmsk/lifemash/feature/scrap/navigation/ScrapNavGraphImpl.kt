package org.bmsk.lifemash.feature.scrap.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.bmsk.lifemash.feature.scrap.ScrapRoute
import org.bmsk.lifemash.feature.scrap.api.ScrapNavGraph
import org.bmsk.lifemash.feature.scrap.api.ScrapNavGraphInfo
import javax.inject.Inject

internal class ScrapNavGraphImpl @Inject constructor() : ScrapNavGraph {
    override fun buildNavGraph(navGraphBuilder: NavGraphBuilder, navInfo: ScrapNavGraphInfo) {
        navGraphBuilder.composable(route = ScrapRoute.ROUTE) {
            ScrapRoute(onShowErrorSnackbar = navInfo.onShowErrorSnackbar)
        }
    }
}
