package org.bmsk.lifemash.feed.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.bmsk.lifemash.feed.domain.repository.FeedPage
import org.bmsk.lifemash.feed.domain.repository.FeedRepository

interface GetFeedUseCase {
    operator fun invoke(cursor: String? = null, limit: Int = 20): Flow<FeedPage>
}

class GetFeedUseCaseImpl(private val repository: FeedRepository) : GetFeedUseCase {
    override fun invoke(cursor: String?, limit: Int) = repository.getFeed(cursor, limit)
}
