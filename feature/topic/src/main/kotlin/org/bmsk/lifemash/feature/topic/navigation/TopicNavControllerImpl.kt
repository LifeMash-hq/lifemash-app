package org.bmsk.lifemash.feature.topic.navigation

import androidx.navigation.NavController
import org.bmsk.lifemash.feature.topic.TopicRoute
import org.bmsk.lifemash.feature.topic.api.TopicNavController
import javax.inject.Inject

internal class TopicNavControllerImpl @Inject constructor() : TopicNavController {
    override fun route(): String = TopicRoute.ROUTE

    override fun navigate(navController: NavController, navInfo: Unit) {
        navController.navigate(TopicRoute.ROUTE)
    }
}