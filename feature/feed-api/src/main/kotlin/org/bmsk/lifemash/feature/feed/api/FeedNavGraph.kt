package org.bmsk.lifemash.feature.feed.api

import org.bmsk.lifemash.feature.nav.LifeMashNavGraph

interface FeedNavGraph : LifeMashNavGraph<FeedNavGraphInfo>

data class FeedNavGraphInfo(
    val onArticleOpen: (String) -> Unit,
)