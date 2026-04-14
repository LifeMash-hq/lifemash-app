package org.bmsk.lifemash.domain.usecase.feed

import org.bmsk.lifemash.domain.feed.FeedComment
import org.bmsk.lifemash.domain.feed.FeedRepository

class CreateFeedCommentUseCase(private val repository: FeedRepository) {
    suspend operator fun invoke(postId: String, content: String): FeedComment =
        repository.createComment(postId, content)
}
