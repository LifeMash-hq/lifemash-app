package org.bmsk.lifemash.feed.data.subscription

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.bmsk.lifemash.feed.data.subscription.datastore.CategorySubscriptionDataSource
import org.bmsk.lifemash.feed.data.subscription.repository.CategorySubscriptionRepositoryImpl
import org.bmsk.lifemash.model.ArticleCategory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CategorySubscriptionRepositoryImplTest {

    private fun createRepository(): CategorySubscriptionRepositoryImpl {
        val stored = MutableStateFlow<Set<String>>(emptySet())
        val fakeDataSource = object : CategorySubscriptionDataSource {
            override fun getSubscribedCategoryKeys(): Flow<Set<String>> = stored
            override suspend fun setSubscribedCategoryKeys(keys: Set<String>) {
                stored.value = keys
            }
        }
        return CategorySubscriptionRepositoryImpl(fakeDataSource)
    }

    @Test
    fun `카테고리 저장 후 조회하면 동일한 Set이 반환된다`() = runTest {
        val repository = createRepository()

        repository.setSubscribedCategories(setOf(ArticleCategory.POLITICS, ArticleCategory.ECONOMY))

        val result = repository.getSubscribedCategories().first()
        assertEquals(setOf(ArticleCategory.POLITICS, ArticleCategory.ECONOMY), result)
    }

    @Test
    fun `빈 Set 저장 시 빈 Set이 반환된다`() = runTest {
        val repository = createRepository()

        repository.setSubscribedCategories(emptySet())

        val result = repository.getSubscribedCategories().first()
        assertEquals(emptySet<ArticleCategory>(), result)
    }
}
