package org.bmsk.lifemash.scrap.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.bmsk.lifemash.scrap.ui.ScrapRoute
import org.bmsk.lifemash.scrap.api.ScrapNavGraph
import org.bmsk.lifemash.scrap.api.ScrapNavGraphInfo
import javax.inject.Inject

internal class ScrapNavGraphImpl @Inject constructor() : ScrapNavGraph {
    override fun buildNavGraph(navGraphBuilder: NavGraphBuilder, navInfo: ScrapNavGraphInfo) {
        navGraphBuilder.composable(route = ScrapRoute.ROUTE) {
            ScrapRoute(
                onClickNews = navInfo.onClickNews,
                onShowErrorSnackbar = navInfo.onShowErrorSnackbar
            )
        }
    }
}
