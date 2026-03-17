package org.bmsk.lifemash.feed.data.subscription.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.bmsk.lifemash.feed.data.subscription.datastore.CategorySubscriptionDataSource
import org.bmsk.lifemash.feed.domain.subscription.repository.CategorySubscriptionRepository
import org.bmsk.lifemash.model.ArticleCategory

class CategorySubscriptionRepositoryImpl(
    private val dataSource: CategorySubscriptionDataSource,
) : CategorySubscriptionRepository {

    override fun getSubscribedCategories(): Flow<Set<ArticleCategory>> =
        dataSource.getSubscribedCategoryKeys().map { keys ->
            keys.map { ArticleCategory.fromKey(it) }.toSet()
        }

    override suspend fun setSubscribedCategories(categories: Set<ArticleCategory>) {
        dataSource.setSubscribedCategoryKeys(
            categories.map { it.key }.toSet()
        )
    }
}
