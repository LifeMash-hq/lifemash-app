package org.bmsk.lifemash.domain.usecase.feed

import org.bmsk.lifemash.domain.feed.FeedComment
import org.bmsk.lifemash.domain.feed.FeedRepository

class GetFeedCommentsUseCase(private val repository: FeedRepository) {
    suspend operator fun invoke(postId: String): List<FeedComment> = repository.getComments(postId)
}
