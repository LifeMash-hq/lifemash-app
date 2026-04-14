package org.bmsk.lifemash.domain.usecase.feed

import org.bmsk.lifemash.domain.feed.FeedRepository

class ToggleFeedLikeUseCase(private val repository: FeedRepository) {
    suspend operator fun invoke(postId: String, isCurrentlyLiked: Boolean): Boolean =
        repository.toggleLike(postId, isCurrentlyLiked)
}
