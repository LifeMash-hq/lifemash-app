package org.bmsk.lifemash.feature.topic.api

import org.bmsk.lifemash.feature.nav.LifeMashNavGraph

interface TopicNavGraph: LifeMashNavGraph<TopicNavGraphInfo>

data class TopicNavGraphInfo(
    val onClickNews: (url: String) -> Unit,
    val onClickScrapPage: () -> Unit,
    val onShowErrorSnackbar: (Throwable?) -> Unit,
)
