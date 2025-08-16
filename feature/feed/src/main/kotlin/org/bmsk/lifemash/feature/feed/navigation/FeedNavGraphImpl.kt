package org.bmsk.lifemash.feature.feed.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.bmsk.lifemash.feature.feed.FeedRoute
import org.bmsk.lifemash.feature.feed.api.FeedNavGraph
import javax.inject.Inject

internal class FeedNavGraphImpl @Inject constructor() : FeedNavGraph {
    override fun buildNavGraph(navGraphBuilder: NavGraphBuilder, navInfo: Unit) {
        navGraphBuilder.composable(route = FeedRoute.ROUTE) {
            FeedRoute()
        }
    }
}