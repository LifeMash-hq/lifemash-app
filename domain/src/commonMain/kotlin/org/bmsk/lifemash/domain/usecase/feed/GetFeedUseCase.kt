package org.bmsk.lifemash.domain.usecase.feed

import org.bmsk.lifemash.domain.feed.FeedFilter
import org.bmsk.lifemash.domain.feed.FeedPage
import org.bmsk.lifemash.domain.feed.FeedRepository

class GetFeedUseCase(private val repository: FeedRepository) {
    suspend operator fun invoke(filter: FeedFilter, cursor: String?, limit: Int): FeedPage =
        repository.getFeed(filter, cursor, limit)
}
