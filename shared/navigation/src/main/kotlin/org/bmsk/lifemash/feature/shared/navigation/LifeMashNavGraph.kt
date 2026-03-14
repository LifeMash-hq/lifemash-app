package org.bmsk.lifemash.feature.shared.navigation

import androidx.navigation.NavGraphBuilder

interface LifeMashNavGraph<T> {
    fun buildNavGraph(navGraphBuilder: NavGraphBuilder, navInfo: T)
}