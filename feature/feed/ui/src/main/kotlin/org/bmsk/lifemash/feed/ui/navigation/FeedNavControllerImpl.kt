package org.bmsk.lifemash.feed.ui.navigation

import androidx.navigation.NavController
import org.bmsk.lifemash.feed.ui.FeedRoute
import org.bmsk.lifemash.feed.api.FeedNavController
import javax.inject.Inject

internal class FeedNavControllerImpl @Inject constructor() : FeedNavController {
    override fun route(): String = FeedRoute.ROUTE

    override fun navigate(navController: NavController, navInfo: Unit) {
        navController.navigate(route())
    }
}