package org.bmsk.lifemash.feature.feed.navigation

import androidx.navigation.NavController
import org.bmsk.lifemash.feature.feed.FeedRoute
import org.bmsk.lifemash.feature.feed.api.FeedNavController
import javax.inject.Inject

internal class FeedNavControllerImpl @Inject constructor() : FeedNavController {
    override fun route(): String = FeedRoute.ROUTE

    override fun navigate(navController: NavController, navInfo: Unit) {
        navController.navigate(route())
    }
}