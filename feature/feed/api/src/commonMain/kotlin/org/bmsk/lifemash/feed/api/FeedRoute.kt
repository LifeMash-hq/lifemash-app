package org.bmsk.lifemash.feed.api

import kotlinx.serialization.Serializable

@Serializable
data object FeedRoute

const val FEED_ROUTE = "feed"

data class FeedNavGraphInfo(
    val onShowErrorSnackbar: (Throwable?) -> Unit,
    val onNavigateToEventDetail: (String) -> Unit,
    val onNavigateToUserProfile: (String) -> Unit,
)
