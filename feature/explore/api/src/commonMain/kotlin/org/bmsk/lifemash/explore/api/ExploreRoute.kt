package org.bmsk.lifemash.explore.api

import kotlinx.serialization.Serializable

@Serializable
data object ExploreRoute

const val EXPLORE_ROUTE = "explore"

data class ExploreNavGraphInfo(
    val onShowErrorSnackbar: (Throwable?) -> Unit,
    val onNavigateToUserProfile: (String) -> Unit,
)
