package org.bmsk.lifemash.core.network.response

import org.bmsk.lifemash.core.model.section.LifeMashCategory
import java.util.Date

data class LifeMashArticle(
    val id: String,
    val title: String,
    val url: String,
    val source: String,
    val pubDate: Date,
    val summary: String? = null,
    val category: LifeMashCategory,
    val imageUrl: String? = null,
)
