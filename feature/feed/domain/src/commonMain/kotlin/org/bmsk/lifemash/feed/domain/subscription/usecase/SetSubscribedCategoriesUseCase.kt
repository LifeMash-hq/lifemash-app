package org.bmsk.lifemash.feed.domain.subscription.usecase

import org.bmsk.lifemash.feed.domain.subscription.repository.CategorySubscriptionRepository
import org.bmsk.lifemash.model.ArticleCategory

interface SetSubscribedCategoriesUseCase {
    suspend operator fun invoke(categories: Set<ArticleCategory>)
}

class SetSubscribedCategoriesUseCaseImpl(
    private val repository: CategorySubscriptionRepository,
) : SetSubscribedCategoriesUseCase {
    override suspend fun invoke(categories: Set<ArticleCategory>) {
        repository.setSubscribedCategories(categories)
    }
}
