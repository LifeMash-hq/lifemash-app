package org.bmsk.lifemash.feed.domain.subscription.usecase

import kotlinx.coroutines.flow.Flow
import org.bmsk.lifemash.feed.domain.subscription.repository.CategorySubscriptionRepository
import org.bmsk.lifemash.model.ArticleCategory

interface GetSubscribedCategoriesUseCase {
    operator fun invoke(): Flow<Set<ArticleCategory>>
}

class GetSubscribedCategoriesUseCaseImpl(
    private val repository: CategorySubscriptionRepository,
) : GetSubscribedCategoriesUseCase {
    override fun invoke(): Flow<Set<ArticleCategory>> = repository.getSubscribedCategories()
}
