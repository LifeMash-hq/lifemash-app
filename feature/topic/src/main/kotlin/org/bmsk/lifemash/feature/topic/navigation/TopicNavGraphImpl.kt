package org.bmsk.lifemash.feature.topic.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.bmsk.lifemash.feature.topic.TopicRoute
import org.bmsk.lifemash.feature.topic.api.TopicNavGraph
import org.bmsk.lifemash.feature.topic.api.TopicNavGraphInfo
import javax.inject.Inject

internal class TopicNavGraphImpl @Inject constructor() : TopicNavGraph {
    override fun buildNavGraph(navGraphBuilder: NavGraphBuilder, navInfo: TopicNavGraphInfo) {
       navGraphBuilder.composable(route = TopicRoute.ROUTE) {
           TopicRoute(
               onClickNews = navInfo.onClickNews,
               onClickScrapPage = navInfo.onClickScrapPage,
               onShowErrorSnackbar = navInfo.onShowErrorSnackbar,
           )
       }
    }
}