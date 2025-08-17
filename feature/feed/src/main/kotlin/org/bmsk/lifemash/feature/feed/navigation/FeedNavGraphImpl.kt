package org.bmsk.lifemash.feature.feed.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.bmsk.lifemash.feature.feed.FeedRoute
import org.bmsk.lifemash.feature.feed.api.FeedNavGraph
import org.bmsk.lifemash.feature.feed.api.FeedNavGraphInfo
import javax.inject.Inject

internal class FeedNavGraphImpl @Inject constructor() : FeedNavGraph {
    override fun buildNavGraph(navGraphBuilder: NavGraphBuilder, navInfo: FeedNavGraphInfo) {
        navGraphBuilder.composable(route = FeedRoute.ROUTE) {
            FeedRoute(navInfo.onArticleOpen)
        }
    }
}