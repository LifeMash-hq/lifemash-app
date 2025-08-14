package org.bmsk.lifemash.core.network.response

import org.bmsk.lifemash.core.model.section.LifeMashSection

data class LifeMashArticle(
    val id: String,
    val title: String,
    val url: String,
    val source: String,
    val published: String,
    val summary: String? = null,
    val section: LifeMashSection,
    val imageUrl: String? = null,
)
