package org.bmsk.lifemash.feed.domain.subscription.repository

import kotlinx.coroutines.flow.Flow
import org.bmsk.lifemash.model.ArticleCategory

interface CategorySubscriptionRepository {
    fun getSubscribedCategories(): Flow<Set<ArticleCategory>>
    suspend fun setSubscribedCategories(categories: Set<ArticleCategory>)
}
