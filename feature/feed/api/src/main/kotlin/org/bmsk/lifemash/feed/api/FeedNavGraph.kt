package org.bmsk.lifemash.feed.api

import org.bmsk.lifemash.feature.shared.navigation.LifeMashNavGraph

interface FeedNavGraph : LifeMashNavGraph<FeedNavGraphInfo>

data class FeedNavGraphInfo(
    val onArticleOpen: (String) -> Unit,
)