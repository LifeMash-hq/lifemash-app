package org.bmsk.lifemash.feature.nav

import androidx.navigation.NavGraphBuilder

interface LifeMashNavGraph<T> {
    fun buildNavGraph(navGraphBuilder: NavGraphBuilder, navInfo: T)
}